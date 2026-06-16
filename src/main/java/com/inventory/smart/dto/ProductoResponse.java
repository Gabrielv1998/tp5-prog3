package com.inventory.smart.dto;

import com.inventory.smart.model.Producto;

/**
 * DTO inmutable para las respuestas que representan un producto.
 * <p>
 * Incluye el nombre de la categoría desnormalizado para conveniencia del cliente.
 * </p>
 *
 * @param id              identificador único del producto
 * @param nombre          nombre del producto
 * @param descripcion     descripción del producto
 * @param precio          precio unitario
 * @param stock           cantidad actual en stock
 * @param categoriaId     identificador de la categoría
 * @param categoriaNombre nombre de la categoría (desnormalizado)
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        double precio,
        int stock,
        Long categoriaId,
        String categoriaNombre
) {

    /**
     * Construye un {@code ProductoResponse} a partir de una entidad {@link Producto}
     * y el nombre de su categoría.
     *
     * @param producto        la entidad producto
     * @param categoriaNombre el nombre de la categoría asociada
     * @return un nuevo {@code ProductoResponse} con los datos del producto
     */
    public static ProductoResponse fromProducto(Producto producto, String categoriaNombre) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoriaId(),
                categoriaNombre
        );
    }
}
