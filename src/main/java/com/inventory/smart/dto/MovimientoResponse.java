package com.inventory.smart.dto;

import com.inventory.smart.model.MovimientoInventario;

import java.time.LocalDateTime;

/**
 * DTO inmutable para las respuestas que representan un movimiento de inventario.
 *
 * @param id         identificador único del movimiento
 * @param productoId identificador del producto asociado
 * @param tipo       tipo de movimiento como cadena ("ENTRADA" o "SALIDA")
 * @param cantidad   cantidad de unidades movidas
 * @param fecha      fecha y hora del movimiento
 * @param motivo     motivo del movimiento
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record MovimientoResponse(
        Long id,
        Long productoId,
        String tipo,
        int cantidad,
        LocalDateTime fecha,
        String motivo
) {

    /**
     * Construye un {@code MovimientoResponse} a partir de una entidad {@link MovimientoInventario}.
     *
     * @param movimiento la entidad movimiento
     * @return un nuevo {@code MovimientoResponse} con los datos del movimiento
     */
    public static MovimientoResponse fromMovimiento(MovimientoInventario movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getProductoId(),
                movimiento.getTipo().name(),
                movimiento.getCantidad(),
                movimiento.getFecha(),
                movimiento.getMotivo()
        );
    }
}
