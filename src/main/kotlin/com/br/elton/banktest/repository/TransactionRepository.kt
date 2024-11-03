package com.br.elton.banktest.repository

import com.br.elton.banktest.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByAccountNumberOrderByTimestampAsc(accountNumber: String): List<Transaction>
}
