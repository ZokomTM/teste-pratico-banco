package com.br.elton.banktest.exception

object ErrorMessages {
    const val ACCOUNT_NOT_FOUND = "Conta não encontrada.\n"
    const val ACCOUNT_NOT_INFORMED = "Conta não informada.\n"
    const val ACCOUNT_TO_NOT_FOUND = "Conta destinatária não encontrada.\n"
    const val ACCOUNT_FROM_NOT_FOUND = "Conta remetente não encontrada.\n"
    const val INSUFFICIENT_BALANCE = "Saldo insuficiente.\n"
    const val AUTHENTICATION_FAILED = "Falha na autenticação.\n"
    const val INVALID_TOKEN = "Token inválido.\n"
    const val INCORRECT_PASSWORD = "Senha incorreta.\n"
    const val INSUFFICIENT_FOUNDS = "Saldo insuficiente.\n"
    const val TRANSACTION_FAILED = "Falha na transação.\n"
    const val DEPOSIT_REQUESTED = "Depósito solicitado.\n"
    const val TRANSFER_REQUESTED = "Transferência solicitada.\n"
    const val WITHDRAW_REQUESTED = "Saque solicitado.\n"
    const val ACCOUNT_NUMBER_NOT_INFORMED = "Número da conta não informado.\n"
    const val INVALID_AMOUNT = "Valor informado para a operação é inválido.\n"
    const val MISSING_FIELDS_IN_CREATE_ACCOUNT = "Campos obrigatórios ausentes na criação da conta.\n"
}
