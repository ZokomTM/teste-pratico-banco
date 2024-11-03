package com.br.elton.banktest.controller

import com.br.elton.banktest.dto.*
import com.br.elton.banktest.model.Account
import com.br.elton.banktest.exception.CustomException
import com.br.elton.banktest.exception.ErrorMessages
import com.br.elton.banktest.service.AccountService
import com.br.elton.banktest.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService,
    private val authService: AuthService
) {

    @PostMapping
    fun createAccount(@RequestBody createAccountDTO: CreateAccountDTO): ResponseEntity<Account> {
        val account = accountService.createAccount(createAccountDTO)
        return ResponseEntity.ok(account)
    }

    @GetMapping
    fun listAccounts(): ResponseEntity<List<AccountDTO>> {
        return ResponseEntity.ok(accountService.listAllAccountsAndTransactions())
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val token = authService.authenticate(authRequest.accountNumber, authRequest.password)
        return ResponseEntity.ok(AuthResponse(token))
    }

    @GetMapping("/{accountNumber}/balance")
    fun getBalance(@PathVariable accountNumber: String, @RequestHeader("Authorization") token: String): ResponseEntity<Double> {
        if (!authService.validateToken(token, TransactionDTO.create(accountNumber))) throw CustomException(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_TOKEN)
        val balance = accountService.getBalance(accountNumber)
        return ResponseEntity.ok(balance)
    }
}
