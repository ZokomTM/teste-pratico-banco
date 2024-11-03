package com.br.elton.banktest.controller

import com.br.elton.banktest.dto.TransactionDTO
import com.br.elton.banktest.exception.CustomException
import com.br.elton.banktest.exception.ErrorMessages
import com.br.elton.banktest.model.Account
import com.br.elton.banktest.model.Transaction
import com.br.elton.banktest.service.AuthService
import com.br.elton.banktest.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val authService: AuthService
) {

    @PostMapping("/deposit")
    fun deposit(@RequestBody transactionDTO: TransactionDTO, @RequestHeader("Authorization") token: String): Account {
        if (!authService.validateToken(token, transactionDTO)) throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_TOKEN)
        return transactionService.deposit(transactionDTO)
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestBody transactionDTO: TransactionDTO, @RequestHeader("Authorization") token: String): Account {
        if (!authService.validateToken(token, transactionDTO)) throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_TOKEN)
        return transactionService.withdraw(transactionDTO)
    }

    @PostMapping("/transfer")
    fun transfer(@RequestBody transactionDTO: TransactionDTO, @RequestHeader("Authorization") token: String) {
        if (!authService.validateToken(token, transactionDTO)) throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_TOKEN)
        return transactionService.transfer(transactionDTO)
    }

    @GetMapping("/{accountNumber}/extract")
    fun getExtract(@PathVariable accountNumber: String, @RequestHeader("Authorization") token: String): ResponseEntity<List<Transaction>> {
        if (!authService.validateToken(token, TransactionDTO(accountNumber))) throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_TOKEN)
        val extract = transactionService.getAccountMovements(accountNumber)
        return ResponseEntity.ok(extract)
    }
}
