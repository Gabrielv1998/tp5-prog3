package com.inventory.smart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO inmutable para la creación o actualización de un producto.
 * <p>
 * Contiene las validaciones de Jakarta Bean Validation que se aplican
 * automáticamente al recibir peticiones en los controladores.
 * </p>
 *
 * @param nombre       nombre del producto (obligatorio)
 * @param descripcion  descripción detallada (mínimo 10, máximo 500 caracteres)
 * @param precio       precio unitario (positivo, obligatorio)
 * @param stockInicial cantidad inicial en stock (cero o positivo)
 * @param categoriaId  identificador de la categoría asociada (obligatorio)
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record ProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        Double precio,

        @PositiveOrZero(message = "El stock inicial no puede ser negativo")
        int stockInicial,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId
) {
}
