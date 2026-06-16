package com.inventory.smart.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio del sistema.
 * <p>
 * Se utiliza para retornar respuestas HTTP 400 (Bad Request). Cubre escenarios como:
 * <ul>
 *   <li>Intentar eliminar una categoría que tiene productos asociados</li>
 *   <li>Intentar duplicar un recurso que debe ser único</li>
 *   <li>Cualquier otra violación de lógica de negocio</li>
 * </ul>
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo de la regla violada.
     *
     * @param message descripción de la regla de negocio violada
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
