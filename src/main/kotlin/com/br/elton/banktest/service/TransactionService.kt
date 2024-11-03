package com.br.elton.banktest.service

import com.br.elton.banktest.dto.TransactionDTO
import com.br.elton.banktest.exception.CustomException
import com.br.elton.banktest.exception.ErrorMessages
import com.br.elton.banktest.model.Account
import com.br.elton.banktest.model.Transaction
import com.br.elton.banktest.repository.AccountRepository
import com.br.elton.banktest.repository.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Classe responsável pela execução de operações financeiras (depósito, saque e transferência) em contas bancárias,
 * e pelo gerenciamento e enfileiramento de transações utilizando o `TransactionQueueService`.
 *
 * @property accountRepository Repositório para gerenciar dados das contas.
 * @property transactionRepository Repositório para gerenciar dados das transações.
 * @property transactionQueueService Serviço para enfileirar e processar transações.
 */
@Service
class TransactionService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionQueueService: TransactionQueueService
) {
    private val logger = LoggerFactory.getLogger(TransactionService::class.java)

    /**
     * Realiza um depósito na conta especificada em `transactionDTO`.
     * A transação é enfileirada para processamento e uma mensagem de confirmação é lançada.
     *
     * @param transactionDTO DTO contendo os dados da transação.
     * @return Mensagem avisando que a transação foi solicitada.
     * @throws CustomException se a conta não for encontrada.
     */
    @Transactional
    fun deposit(transactionDTO: TransactionDTO): Account {
        this.validTransactionDTO(transactionDTO, false)

        val accountNumber: String = transactionDTO.accountNumber!!
        val amount: Double = transactionDTO.amount!!
        transactionQueueService.addTransaction(accountNumber) {
            val account = accountRepository.findByAccountNumber(accountNumber)

            if(account == null){
                transactionRepository.save(Transaction.createDeposit(accountNumber, amount, ErrorMessages.ACCOUNT_NOT_FOUND))
                throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_NOT_FOUND)
            }

            account.balance += amount
            accountRepository.save(account)
            transactionRepository.save(Transaction.createDeposit(accountNumber, amount, null))
            logger.info("Deposited $amount to account number: $accountNumber. New balance: ${account.balance}")
        }

        throw CustomException(HttpStatus.OK, ErrorMessages.DEPOSIT_REQUESTED)
    }

    /**
     * Realiza um saque na conta especificada em `transactionDTO`.
     * A transação é enfileirada para processamento e uma mensagem de confirmação é lançada.
     *
     * @param transactionDTO DTO contendo os dados da transação.
     * @return Mensagem avisando que a transação foi solicitada.
     * @throws CustomException se a conta não for encontrada ou saldo insuficiente.
     */
    @Transactional
    fun withdraw(transactionDTO: TransactionDTO): Account {
        this.validTransactionDTO(transactionDTO, false)

        val accountNumber: String = transactionDTO.accountNumber!!
        val amount: Double = transactionDTO.amount!!
        transactionQueueService.addTransaction(accountNumber) {
            val account = accountRepository.findByAccountNumber(accountNumber)

            if(account == null){
                transactionRepository.save(Transaction.createWithdrawal(accountNumber, amount, ErrorMessages.ACCOUNT_NOT_FOUND))
                throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_NOT_FOUND)
            }

            if (account.balance < amount) {
                transactionRepository.save(Transaction.createWithdrawal(accountNumber, amount, ErrorMessages.INSUFFICIENT_FOUNDS))
                throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.INSUFFICIENT_FOUNDS)
            }

            account.balance -= amount
            accountRepository.save(account)

            transactionRepository.save(Transaction.createWithdrawal(accountNumber, amount, null))
            logger.info("Withdrew $amount from account number: $accountNumber. New balance: ${account.balance}")
        }
        throw CustomException(HttpStatus.OK, ErrorMessages.WITHDRAW_REQUESTED)
    }

    /**
     * Realiza uma transferência entre contas especificadas em `transactionDTO`.
     * A transação é enfileirada para processamento e uma mensagem avisando que a transação foi solicitada é lançada.
     *
     * @param transactionDTO DTO contendo os dados da transferência.
     * @return Mensagem avisando que a transação foi solicitada.
     * @throws CustomException se a conta de origem ou destino não for encontrada ou saldo insuficiente.
     */
    @Transactional
    fun transfer(transactionDTO: TransactionDTO) {
        this.validTransactionDTO(transactionDTO, true)

        val fromAccountNumber: String = transactionDTO.fromAccount!!
        val toAccountNumber: String = transactionDTO.toAccount!!
        val amount: Double = transactionDTO.amount!!

        /**
         * Adiciona transação da conta que fez a transferencia na fila para execução
         */
        transactionQueueService.addTransaction(fromAccountNumber) {
            val fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
            if(fromAccount == null){
                transactionRepository.save(Transaction.createTransferOut(fromAccountNumber, toAccountNumber, amount, ErrorMessages.ACCOUNT_FROM_NOT_FOUND ))
                throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_FROM_NOT_FOUND)
            }
            if (fromAccount.balance < amount) {
                transactionRepository.save(Transaction.createTransferOut(fromAccountNumber, toAccountNumber, amount, ErrorMessages.INSUFFICIENT_FOUNDS))
                throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.INSUFFICIENT_FOUNDS)
            }

            fromAccount.balance -= amount

            accountRepository.save(fromAccount)
            transactionRepository.save(Transaction.createTransferOut(fromAccountNumber, toAccountNumber, amount, null))

            /**
             * Adiciona transação da conta que recebeu a transferencia na fila para execução
             */
            transactionQueueService.addTransaction(toAccountNumber) {
                val toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                if(toAccount == null){
                    transactionRepository.save(Transaction.createTransferOut(fromAccountNumber, toAccountNumber, amount, ErrorMessages.ACCOUNT_TO_NOT_FOUND))
                    throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_TO_NOT_FOUND)
                }

                toAccount.balance += amount

                accountRepository.save(toAccount)
                transactionRepository.save(Transaction.createTransferIn(fromAccountNumber, toAccountNumber, amount, null))
            }

            logger.info("Transferred $amount from account number: $fromAccountNumber to account number: $toAccountNumber")
        }
        throw CustomException(HttpStatus.OK, ErrorMessages.TRANSFER_REQUESTED)
    }

    /**
     * Obtém o histórico de movimentações para a conta informada.
     *
     * @param accountNumber Número da conta para a qual obter as transações.
     * @return Lista de transações da conta.
     */
    fun getAccountMovements(accountNumber: String): List<Transaction> {
        logger.info("Retrieving movements for account number: $accountNumber")
        return transactionRepository.findByAccountNumberOrderByTimestampAsc(accountNumber)
    }

    /**
     * Valida o DTO de transação, garantindo que todos os campos obrigatórios estejam preenchidos.
     *
     * @param transactionDTO DTO contendo os dados da transação.
     * @param validTransfer Define se a validação deve considerar uma transferência.
     * @throws CustomException se algum campo obrigatório estiver ausente ou valor inválido.
     */
    fun validTransactionDTO(transactionDTO: TransactionDTO, validTransfer: Boolean) {
        if(transactionDTO.accountNumber.isNullOrEmpty()) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_NUMBER_NOT_INFORMED)
        if(transactionDTO.amount == null || transactionDTO.amount <= 0.0) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_AMOUNT)
        if(validTransfer && transactionDTO.fromAccount.isNullOrEmpty()) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_FROM_NOT_FOUND)
        if(validTransfer && transactionDTO.toAccount.isNullOrEmpty()) throw CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ACCOUNT_TO_NOT_FOUND)
    }
}
