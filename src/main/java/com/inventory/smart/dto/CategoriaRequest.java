package com.inventory.smart.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO inmutable para la creación o actualización de una categoría.
 *
 * @param nombre      nombre de la categoría (obligatorio)
 * @param descripcion descripción de la categoría (obligatorio)
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción de la categoría es obligatoria")
        String descripcion
) {
}
