package com.inventory.smart.dto;

import com.inventory.smart.model.NivelAlerta;

/**
 * DTO inmutable que representa una alerta de stock bajo para un producto.
 *
 * @param productoId  identificador del producto
 * @param nombre      nombre del producto
 * @param stockActual cantidad actual en stock
 * @param stockMinimo umbral mínimo configurado
 * @param nivel       nivel de alerta: NORMAL, BAJO o CRITICO
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public record AlertaStockResponse(
        Long productoId,
        String nombre,
        int stockActual,
        int stockMinimo,
        NivelAlerta nivel
) {

    /**
     * Construye una {@code AlertaStockResponse} con los datos de un producto
     * y el nivel de alerta evaluado.
     *
     * @param productoId  identificador del producto
     * @param nombre      nombre del producto
     * @param stockActual cantidad actual en stock
     * @param stockMinimo umbral mínimo configurado
     * @param nivel       nivel de alerta evaluado
     * @return una nueva {@code AlertaStockResponse}
     */
    public static AlertaStockResponse of(Long productoId, String nombre, int stockActual,
                                          int stockMinimo, NivelAlerta nivel) {
        return new AlertaStockResponse(productoId, nombre, stockActual, stockMinimo, nivel);
    }
}
