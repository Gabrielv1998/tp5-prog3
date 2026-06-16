package com.inventory.smart.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Centraliza el manejo de todas las excepciones de la aplicación, asegurando
 * que el cliente reciba respuestas de error consistentes con la estructura
 * definida en {@link ErrorResponse}.
 * </p>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 Not Found</li>
 *   <li>{@link InsufficientStockException} → 409 Conflict</li>
 *   <li>{@link BusinessRuleException} → 400 Bad Request</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (errores de validación)</li>
 *   <li>Cualquier otra excepción → 500 Internal Server Error</li>
 * </ul>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja {@link ResourceNotFoundException} retornando HTTP 404.
     *
     * @param ex      la excepción capturada
     * @param request la petición web actual
     * @return respuesta con estado 404 y detalles del error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                 WebRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja {@link InsufficientStockException} retornando HTTP 409 Conflict.
     *
     * @param ex      la excepción capturada
     * @param request la petición web actual
     * @return respuesta con estado 409 y detalles del conflicto
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex,
                                                                  WebRequest request) {
        log.warn("Conflicto de stock: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "Conflicto de stock",
                ex.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja {@link BusinessRuleException} retornando HTTP 400 Bad Request.
     *
     * @param ex      la excepción capturada
     * @param request la petición web actual
     * @return respuesta con estado 400 y descripción de la regla violada
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex,
                                                             WebRequest request) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Regla de negocio violada",
                ex.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validación de Jakarta Bean Validation ({@code @Valid})
     * retornando HTTP 400 con los campos que fallaron la validación.
     *
     * @param ex      la excepción de validación
     * @param request la petición web actual
     * @return respuesta con estado 400 y lista de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                 WebRequest request) {
        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Errores de validación: {}", fieldErrors);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                fieldErrors,
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja cualquier excepción no contemplada retornando HTTP 500.
     *
     * @param ex      la excepción genérica
     * @param request la petición web actual
     * @return respuesta con estado 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, WebRequest request) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ocurrió un error inesperado. Contacte al administrador.",
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Extrae la ruta de la petición para incluirla en la respuesta de error.
     *
     * @param request la petición web
     * @return la ruta del endpoint, o "desconocida" si no se puede determinar
     */
    private String extractPath(WebRequest request) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("uri=")) {
            return path.substring(4);
        }
        return request.getDescription(false);
    }
}
