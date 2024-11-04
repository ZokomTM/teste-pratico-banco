package com.br.elton.banktest.service

import com.br.elton.banktest.dto.TransactionDTO
import com.br.elton.banktest.exception.CustomException
import com.br.elton.banktest.exception.ErrorMessages
import com.br.elton.banktest.repository.AccountRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Base64
import org.springframework.http.HttpStatus
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service de autenticação para gerenciar a autenticação de contas.
 *
 * @param accountRepository O repositório de contas para operações de busca.
 */
@Service
class AuthService (
    private val accountRepository: AccountRepository
){
    private val logger = LoggerFactory.getLogger(AccountService::class.java)
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)

    /**
     * Valida a conta, confirmando se a senha está correta e também gera um Token JWT para utilizar nas outras chamadas,
     * Token com validade de 3H.
     *
     * @param accountNumber O número da conta a ser autenticada.
     * @param password A senha da conta a ser autenticada.
     * @return Um token JWT gerado para a conta autenticada (validade 3H).
     * @throws CustomException Se a conta não for encontrada ou se a senha estiver incorreta.
     */
    fun authenticate(accountNumber: String, password: String): String {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw CustomException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_NOT_FOUND)

        if (account.password != password) {
            logger.warn("Failed authentication attempt for account number: $accountNumber")
            throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INCORRECT_PASSWORD)
        }

        val token = Jwts.builder()
            .setSubject(accountNumber)
            .setExpiration(Date(System.currentTimeMillis() + (60 * 60 * 3000)))
            .signWith(secretKey)
            .compact()

        logger.info("Account authenticated successfully: $accountNumber")
        return token
    }

    /**
     * Valida se o token JWT está válido de acordo com a conta informada.
     *
     * @param token O token JWT a ser validado.
     * @param transactionDTO O DTO de transação que contém o número da conta.
     * @return Verdadeiro se o token for válido e corresponder ao número da conta, falso caso contrário.
     * @throws CustomException Se o número da conta não for informado.
     */
    fun validateToken(token: String, transactionDTO: TransactionDTO): Boolean {
        if (transactionDTO.accountNumber.isNullOrBlank()) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_NOT_INFORMED)
        return try {
            val tokenWithoutBearer = token.removePrefix("Bearer ").trim()
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(tokenWithoutBearer)
                .body
            (!claims.subject.isNullOrBlank() && claims.subject.equals(transactionDTO.accountNumber))
        } catch (e: Exception) {
            logger.error("Token validation failed: ${e.message}", e)
            false
        }
    }
}