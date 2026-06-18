package com.inventory.smart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
        @Size(min = 3, max = 50,
                message = "El nombre debe tener entre 3 y 50 caracteres"
        )
        String nombre,

        @NotBlank(message = "La descripción de la categoría es obligatoria")
        @Size(min = 10, max = 200,
                message = "La descripcion debe tener entre 10 y 200 caracteres"
        )
        String descripcion
) {
}
