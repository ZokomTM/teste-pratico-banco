package com.br.elton.banktest.exception

import org.springframework.http.HttpStatus

/**
 * Classe `CustomException` é uma exceção personalizada que estende `RuntimeException`.
 *
 * Esta classe é usada para encapsular informações de erro específicas, incluindo um status HTTP
 * e uma mensagem de erro. Ela permite um tratamento de exceções mais granular e informativo na aplicação.
 *
 * @param status O status HTTP associado à exceção.
 * @param message A mensagem de erro que descreve a exceção.
 */
class CustomException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)
