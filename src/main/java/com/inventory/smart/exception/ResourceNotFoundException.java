package com.inventory.smart.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * <p>
 * Se utiliza para retornar respuestas HTTP 404 (Not Found). Almacena información
 * adicional sobre el recurso buscado para facilitar el diagnóstico.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /** Nombre del recurso (ej: "Producto", "Categoria"). */
    private final String resourceName;

    /** Nombre del campo usado para buscar. */
    private final String fieldName;

    /** Valor del campo buscado. */
    private final transient Object fieldValue;

    /**
     * Construye la excepción con los detalles completos de la búsqueda fallida.
     *
     * @param resourceName nombre del recurso buscado
     * @param fieldName    nombre del campo de búsqueda
     * @param fieldValue   valor del campo buscado
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Construye la excepción usando un identificador como campo de búsqueda.
     * <p>
     * Este constructor acepta cualquier tipo de identificador ({@link Long}, {@link String}, etc.)
     * a través del tipo {@link Object}, manteniendo flexibilidad para distintos repositorios.
     * </p>
     *
     * @param resourceName nombre del recurso (ej: "Producto", "Categoria")
     * @param id           identificador buscado
     */
    public ResourceNotFoundException(String resourceName, Object id) {
        this(resourceName, "id", id);
    }

    /**
     * @return el nombre del recurso
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return el nombre del campo de búsqueda
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return el valor del campo buscado
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
