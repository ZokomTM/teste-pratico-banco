package com.br.elton.banktest.service

import com.br.elton.banktest.dto.AccountDTO
import com.br.elton.banktest.dto.CreateAccountDTO
import com.br.elton.banktest.model.Account
import com.br.elton.banktest.repository.AccountRepository
import com.br.elton.banktest.exception.CustomException
import com.br.elton.banktest.exception.ErrorMessages
import com.br.elton.banktest.repository.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.locks.ReentrantLock

/**
 * Service responsável pela gestão de contas bancárias, incluindo criação de contas,
 * listagem de contas com transações e obtenção de saldo.
 *
 * @property accountRepository Repositório de contas para realizar operações de banco de dados.
 * @property transactionRepository Repositório de transações para operações associadas a contas.
 */
@Service
class AccountService(
    val accountRepository: AccountRepository,
    val transactionRepository: TransactionRepository
) {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)
    private val accountLocks = mutableMapOf<String, ReentrantLock>()

    /**
     * Cria uma nova conta bancária com base nos dados fornecidos no DTO.
     *
     * @param createAccountDTO Dados necessários para a criação de uma nova conta.
     * @return A conta criada e salva no repositório.
     * @throws CustomException Se houver campos obrigatórios ausentes no DTO de criação.
     */
    @Transactional
    fun createAccount(createAccountDTO: CreateAccountDTO): Account{
        if(!CreateAccountDTO.validDTO(createAccountDTO)) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.MISSING_FIELDS_IN_CREATE_ACCOUNT)

        val account = Account.newAccount(createAccountDTO, accountRepository)
        accountLocks[account.accountNumber] = ReentrantLock()

        val savedAccount = accountRepository.save(account)
        logger.info("Account created successfully: $savedAccount")
        return savedAccount
    }

    /**
     * Lista todas as contas e suas transações associadas, normalmente esse metodo não teria, porem criei apenas para demonstrar
     *
     * @return Uma lista de objetos `AccountDTO` representando todas as contas e transações.
     */
    fun listAllAccountsAndTransactions(): List<AccountDTO> {
        val accounts: List<Account> = accountRepository.findAll()
        val accountsReturn = accounts.map { account ->
            AccountDTO.fromAccount(account, transactionRepository)
        }
        return accountsReturn
    }

    /**
     * Obtém o saldo de uma conta específica.
     *
     * @param accountNumber Número da conta para a qual o saldo será recuperado.
     * @return O saldo atual da conta.
     * @throws CustomException Se a conta não for encontrada.
     */
    fun getBalance(accountNumber: String): Double {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw CustomException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_NOT_FOUND)
        logger.info("Retrieved balance for account number: $accountNumber - Balance: ${account.balance}")
        return account.balance
    }
}
