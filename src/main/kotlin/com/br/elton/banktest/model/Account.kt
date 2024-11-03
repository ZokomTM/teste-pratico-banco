package com.br.elton.banktest.model

import com.br.elton.banktest.dto.CreateAccountDTO
import com.br.elton.banktest.repository.AccountRepository
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Classe `Account` representa uma conta bancária no sistema.
 *
 * @property id Identificador único da conta.
 * @property accountNumber Número da conta, gerado de forma única.
 * @property clientName Nome do cliente titular da conta.
 * @property openingDate Data de abertura da conta.
 * @property balance Saldo atual da conta.
 * @property password Senha associada à conta, não é exposta na serialização JSON.
 */
@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val accountNumber: String,
    val clientName: String,
    val openingDate: LocalDateTime,
    var balance: Double,

    @JsonIgnore
    val password: String,
) {
    companion object {
        /**
         * Cria uma nova conta a partir de um objeto `CreateAccountDTO`.
         *
         * @param createAccountDTO Objeto contendo os dados necessários para a criação de uma conta.
         * @param accountRepository Repositório de contas para verificar duplicidade de números de conta.
         * @return Uma nova instância de `Account` com o número de conta gerado de forma única.
         */
        fun newAccount(createAccountDTO: CreateAccountDTO, accountRepository: AccountRepository): Account {
            return Account(
                accountNumber = this.generateUniqueAccountNumber(accountRepository),
                clientName = createAccountDTO.clientName,
                openingDate = LocalDateTime.now(),
                balance = createAccountDTO.initialBalance,
                password = createAccountDTO.password
            )
        }

        /**
         * Gera um número de conta único verificando sua existência no repositório.
         *
         * @param accountRepository Repositório de contas usado para verificar se o número gerado já existe.
         * @return Um número de conta único no formato "#####-#", onde # é um dígito numérico.
         */
        private fun generateUniqueAccountNumber(accountRepository: AccountRepository): String {
            var accountNumber: String
            do {
                accountNumber = "${Random.nextInt(10000, 99999)}-${Random.nextInt(0, 9)}"
            } while (accountRepository.existsByAccountNumber(accountNumber))
            return accountNumber
        }
    }
}
