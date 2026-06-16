package com.inventory.smart.dto;

import com.inventory.smart.model.Categoria;

/**
 * DTO inmutable para las respuestas que representan una categoría.
 *
 * @param id          identificador único de la categoría
 * @param nombre      nombre de la categoría
 * @param descripcion descripción de la categoría
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion
) {

    /**
     * Construye un {@code CategoriaResponse} a partir de una entidad {@link Categoria}.
     *
     * @param categoria la entidad categoría
     * @return un nuevo {@code CategoriaResponse} con los datos de la categoría
     */
    public static CategoriaResponse fromCategoria(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
