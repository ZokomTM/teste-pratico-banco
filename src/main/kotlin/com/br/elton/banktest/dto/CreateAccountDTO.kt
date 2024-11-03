package com.br.elton.banktest.dto

/**
 * Classe `CreateAccountDTO` representa o objeto de transferência de dados (DTO)
 * utilizado para criar uma nova conta bancária.
 *
 * Esta classe contém informações necessárias para a criação de uma conta,
 * incluindo o nome do cliente, o saldo inicial e a senha.
 *
 * @property clientName O nome do cliente que está criando a conta.
 * @property initialBalance O saldo inicial da conta a ser criada, deve ser um valor não negativo.
 * @property password A senha que será utilizada para autenticação da conta.
 */
data class CreateAccountDTO(
    val clientName: String,
    val initialBalance: Double,
    val password: String
) {
    companion object {
        /**
         * Valida se os dados fornecidos para criar uma conta são válidos.
         *
         * @param createAccountDTO O objeto contendo os dados a serem validados.
         * @return `true` se os dados forem válidos, caso contrário `false`.
         *
         * As regras de validação incluem:
         * - O nome do cliente não deve ser uma string vazia.
         * - O saldo inicial deve ser um valor não negativo.
         * - A senha não deve ser uma string vazia.
         */
        fun validDTO(createAccountDTO: CreateAccountDTO): Boolean {
            if(createAccountDTO.clientName.isBlank()) return false
            if(createAccountDTO.initialBalance < 0) return false
            return createAccountDTO.password.isNotBlank()
        }
    }
}
