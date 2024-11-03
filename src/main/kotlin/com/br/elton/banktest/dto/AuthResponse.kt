package com.br.elton.banktest.dto

/**
 * Classe `AuthResponse` representa a resposta a um pedido de autenticação.
 *
 * Esta classe é utilizada para encapsular o token de autenticação que é retornado
 * após um cliente se autenticar com sucesso no sistema bancário.
 *
 * @property token O token de autenticação gerado, que será utilizado para
 * autenticar futuras requisições do cliente.
 */
data class AuthResponse(
    val token: String
)
