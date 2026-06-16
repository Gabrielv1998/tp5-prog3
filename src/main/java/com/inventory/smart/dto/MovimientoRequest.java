package com.inventory.smart.dto;

import com.inventory.smart.model.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO inmutable para el registro de un movimiento de inventario.
 *
 * @param productoId identificador del producto (obligatorio)
 * @param tipo       tipo de movimiento: ENTRADA o SALIDA (obligatorio)
 * @param cantidad   cantidad de unidades (positiva, obligatoria)
 * @param motivo     motivo o justificación del movimiento (obligatorio)
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record MovimientoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        @Positive(message = "La cantidad debe ser positiva")
        int cantidad,

        @NotBlank(message = "El motivo del movimiento es obligatorio")
        String motivo
) {
}
