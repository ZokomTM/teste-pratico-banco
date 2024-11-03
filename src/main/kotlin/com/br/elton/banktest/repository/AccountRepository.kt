package com.br.elton.banktest.repository

import com.br.elton.banktest.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByAccountNumber(accountNumber: String): Account?
    fun existsByAccountNumber(accountNumber: String): Boolean
}

