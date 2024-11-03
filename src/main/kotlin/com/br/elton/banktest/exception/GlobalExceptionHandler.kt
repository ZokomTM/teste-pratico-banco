package com.br.elton.banktest.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Classe `GlobalExceptionHandler` é responsável por capturar e tratar exceções personalizadas lançadas na aplicação.
 *
 * Esta classe utiliza a anotação `@RestControllerAdvice`, que permite que a classe trate exceções em todos os controladores da aplicação.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Método que lida com exceções do tipo `CustomException`.
     *
     * @param ex A exceção personalizada capturada.
     * @return Um objeto `ResponseEntity` contendo a mensagem da exceção e o status HTTP associado.
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<String> {
        return ResponseEntity(ex.message, ex.status)
    }
}
