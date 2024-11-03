package com.br.elton.banktest.dto

import java.time.LocalDateTime

/**
 * Classe `TransactionDTO` representa um objeto de transferência de dados (DTO) que
 * contém informações sobre uma transação bancária. Esta classe é utilizada para
 * encapsular os dados necessários para registrar ou consultar transações.
 *
 * @property accountNumber O número da conta associada à transação.
 * @property fromAccount O número da conta de origem (caso seja uma transferência).
 * @property toAccount O número da conta de destino (caso seja uma transferência).
 * @property amount O valor da transação.
 * @property failed Indica se a transação falhou.
 * @property error Mensagem de erro, caso a transação tenha falhado.
 * @property type Tipo da transação (ex.: DEPÓSITO, RETIRADA, TRANSFERÊNCIA).
 * @property timestamp Data e hora em que a transação ocorreu.
 */
data class TransactionDTO(
    val accountNumber: String? = null,
    val fromAccount: String? = null,
    val toAccount: String? = null,
    val amount: Double? = null,
    val failed: Boolean = false,
    val error: String? = null,
    val type: String? = null,
    val timestamp: LocalDateTime? = null
) {
    companion object {
        /**
         * Cria uma nova instância de `TransactionDTO` com o número da conta fornecido.
         *
         * @param accountNumber O número da conta para a transação.
         * @return Uma nova instância de `TransactionDTO` com o número da conta fornecido.
         */
        fun create(accountNumber: String): TransactionDTO {
            return TransactionDTO(accountNumber)
        }
    }
}