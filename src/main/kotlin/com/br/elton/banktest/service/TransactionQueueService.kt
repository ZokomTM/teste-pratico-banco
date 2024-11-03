package com.br.elton.banktest.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

/**
 * Serviço que gerencia uma fila de transações para cada conta individualmente.
 * Cada conta possui sua própria fila e executor de thread, permitindo processamento independente.
 */
@Service
class TransactionQueueService {

    private val logger = LoggerFactory.getLogger(TransactionQueueService::class.java)
    private val accountQueues = ConcurrentHashMap<String, ConcurrentLinkedQueue<() -> Unit>>()
    private val accountExecutors = ConcurrentHashMap<String, ExecutorService>()

    /**
     * Adiciona uma transação à fila da conta especificada e inicia o processamento dessa fila.
     *
     * @param accountNumber Número da conta a que a transação pertence.
     * @param transaction Função que representa a transação a ser processada.
     */
    fun addTransaction(accountNumber: String, transaction: () -> Unit) {
        val queue = accountQueues.computeIfAbsent(accountNumber) { ConcurrentLinkedQueue() }
        queue.add(transaction)

//        logger.info("Transaction added to account queue: $accountNumber. Items in the queue: ${queue.size}")

        val executor = accountExecutors.computeIfAbsent(accountNumber) { Executors.newSingleThreadExecutor() }
        processQueue(accountNumber, queue, executor)
    }

    /**
     * Processa a fila de transações para uma conta específica, executando cada transação sequencialmente.
     * Caso ocorra um erro, ele será registrado no log.
     *
     * @param accountNumber Número da conta cuja fila será processada.
     * @param queue Fila de transações da conta.
     * @param executor Executor responsável por processar as transações na fila.
     */
    private fun processQueue(accountNumber: String, queue: ConcurrentLinkedQueue<() -> Unit>, executor: ExecutorService) {
        executor.submit {
            while (queue.isNotEmpty()) {
                val transaction = queue.poll()
                try {
//                    logger.info("Transaction executes to account: $accountNumber. Items in the queue: ${queue.size}")
                    transaction?.invoke()
                    Thread.sleep(1000) // 1 segundo apenas para melhorar nas demonstrações
                } catch (e: Exception) {
                    logger.error("Error processing transaction for account $accountNumber: ${e.message}", e)
                }
            }
        }
    }
}
