package com.inventory.smart.exception;

import java.time.LocalDateTime;

/**
 * Record inmutable que representa una respuesta de error estandarizada para la API REST.
 * <p>
 * Todas las respuestas de error del sistema siguen esta estructura consistente,
 * facilitando el manejo de errores por parte del cliente.
 * </p>
 *
 * @param timestamp momento en que ocurrió el error
 * @param status    código de estado HTTP
 * @param error     descripción breve del tipo de error
 * @param message   mensaje detallado del error
 * @param path      endpoint donde ocurrió el error
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {

    /**
     * Construye un {@code ErrorResponse} con la marca de tiempo actual.
     *
     * @param status  código de estado HTTP
     * @param error   descripción breve del tipo de error
     * @param message mensaje detallado
     * @param path    endpoint involucrado
     * @return un nuevo {@code ErrorResponse}
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
}
