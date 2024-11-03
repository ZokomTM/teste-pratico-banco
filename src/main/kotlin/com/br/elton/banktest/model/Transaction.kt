package com.br.elton.banktest.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Classe `Transaction` representa uma transação financeira no sistema bancário.
 *
 * @property id Identificador único da transação.
 * @property accountNumber Número da conta associada à transação.
 * @property accountFrom Número da conta de origem (para transferências).
 * @property accountTo Número da conta de destino (para transferências).
 * @property failed Indica se a transação falhou.
 * @property error Mensagem de erro associada à transação, caso tenha falhado.
 * @property type Tipo de transação (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT).
 * @property amount Valor da transação.
 * @property timestamp Data e hora em que a transação ocorreu.
 */
@Entity
data class Transaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val accountNumber: String,
    val accountFrom: String,
    val accountTo: String,
    val failed: Boolean,
    val error: String?,
    val type: String,
    val amount: Double,
    val timestamp: LocalDateTime,
) {
    companion object {
        /**
         * Cria uma transação de transferência de saída (TRANSFER_OUT).
         *
         * @param accountFrom Número da conta de origem.
         * @param accountTo Número da conta de destino.
         * @param amount Valor da transferência.
         * @param error Mensagem de erro associada à transação, se houver.
         * @return Uma nova instância de `Transaction` representando uma transferência de saída.
         */
        fun createTransferOut(accountFrom: String, accountTo: String, amount: Double, error: String?): Transaction {
            return Transaction(
                accountNumber = accountFrom,
                accountFrom = accountFrom,
                accountTo = accountTo,
                type = "TRANSFER_OUT",
                amount = amount,
                error = error,
                failed = !error.isNullOrEmpty(),
                timestamp = LocalDateTime.now()
            )
        }

        /**
         * Cria uma transação de transferência de entrada (TRANSFER_IN).
         *
         * @param accountFrom Número da conta de origem.
         * @param accountTo Número da conta de destino.
         * @param amount Valor da transferência.
         * @param error Mensagem de erro associada à transação, se houver.
         * @return Uma nova instância de `Transaction` representando uma transferência de entrada.
         */
        fun createTransferIn(accountFrom: String, accountTo: String, amount: Double, error: String?): Transaction {
            return Transaction(
                accountNumber = accountTo,
                accountFrom = accountFrom,
                accountTo = accountTo,
                type = "TRANSFER_IN",
                amount = amount,
                error = error,
                failed = !error.isNullOrEmpty(),
                timestamp = LocalDateTime.now()
            )
        }

        /**
         * Cria uma transação de depósito (DEPOSIT).
         *
         * @param accountNumber Número da conta associada ao depósito.
         * @param amount Valor do depósito.
         * @param error Mensagem de erro associada à transação, se houver.
         * @return Uma nova instância de `Transaction` representando um depósito.
         */
        fun createDeposit(accountNumber: String, amount: Double, error: String?): Transaction {
            return Transaction(
                accountNumber = accountNumber,
                accountFrom = accountNumber,
                accountTo = accountNumber,
                type = "DEPOSIT",
                amount = amount,
                error = error,
                failed = !error.isNullOrEmpty(),
                timestamp = LocalDateTime.now()
            )
        }

        /**
         * Cria uma transação de saque (WITHDRAWAL).
         *
         * @param accountNumber Número da conta associada ao saque.
         * @param amount Valor do saque.
         * @param error Mensagem de erro associada à transação, se houver.
         * @return Uma nova instância de `Transaction` representando um saque.
         */
        fun createWithdrawal(accountNumber: String, amount: Double, error: String?): Transaction {
            return Transaction(
                accountNumber = accountNumber,
                accountFrom = accountNumber,
                accountTo = accountNumber,
                type = "WITHDRAWAL",
                amount = amount,
                error = error,
                failed = !error.isNullOrEmpty(),
                timestamp = LocalDateTime.now()
            )
        }
    }
}
