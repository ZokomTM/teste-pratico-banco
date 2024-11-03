package com.br.elton.banktest.dto

import com.br.elton.banktest.model.Account
import com.br.elton.banktest.model.Transaction
import com.br.elton.banktest.repository.TransactionRepository
import java.time.LocalDateTime

/**
 * Classe `AccountDTO` representa um objeto de transferência de dados para a entidade `Account`.
 *
 * Esta classe é utilizada para transportar informações sobre uma conta, incluindo detalhes da conta
 * e suas transações associadas utilizado para retorno no endpoint.
 *
 * @property id O identificador único da conta.
 * @property accountNumber O número da conta.
 * @property clientName O nome do cliente associado à conta.
 * @property openingDate A data e hora em que a conta foi aberta.
 * @property balance O saldo atual da conta.
 * @property transactions A lista de transações associadas à conta.
 */
data class AccountDTO(
    val id: Long,
    val accountNumber: String,
    val clientName: String,
    val openingDate: LocalDateTime,
    var balance: Double,
    var transactions: List<Transaction> = emptyList()
) {
    companion object {
        /**
         * Constrói uma instância de `AccountDTO` a partir de uma instância de `Account` e de um repositório de transações.
         *
         * Este método busca as transações associadas a uma conta específica e cria um `AccountDTO` que
         * encapsula todas as informações relevantes da conta e suas transações.
         *
         * @param account A instância da conta que será convertida em DTO.
         * @param transactionRepository O repositório utilizado para buscar as transações da conta.
         * @return Uma instância de `AccountDTO` contendo os dados da conta e suas transações.
         */
        fun fromAccount(account: Account, transactionRepository: TransactionRepository): AccountDTO {
            val transactions = transactionRepository.findByAccountNumberOrderByTimestampAsc(account.accountNumber)
            return AccountDTO(
                id = account.id,
                accountNumber = account.accountNumber,
                clientName = account.clientName,
                openingDate = account.openingDate,
                balance = account.balance,
                transactions = transactions
            )
        }
    }
}
