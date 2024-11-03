package com.br.elton.banktest.dto

/**
 * Classe `AuthRequest` representa um pedido de autenticação de um usuário.
 *
 * Esta classe é utilizada para encapsular as informações necessárias para
 * autenticar um usuário em um sistema bancário.
 * Inclui o número da conta do cliente e a senha associada.
 *
 * @property accountNumber O número da conta do cliente que deseja autenticar.
 * @property password A senha da conta do cliente que será usada para autenticação.
 */
data class AuthRequest(
    val accountNumber: String,
    val password: String
)
