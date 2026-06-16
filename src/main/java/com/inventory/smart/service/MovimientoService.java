package com.inventory.smart.service;

import com.inventory.smart.dto.MovimientoRequest;
import com.inventory.smart.dto.MovimientoResponse;

import java.util.List;

/**
 * Servicio de negocio para la gestión de movimientos de inventario.
 * <p>
 * Define operaciones para registrar entradas/salidas y consultar el historial
 * de movimientos de un producto.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface MovimientoService {

    /**
     * Registra un nuevo movimiento de inventario (entrada o salida).
     * <p>
     * Para movimientos de tipo SALIDA, verifica que haya stock suficiente.
     * Actualiza atómicamente el stock del producto.
     * </p>
     *
     * @param request datos del movimiento a registrar
     * @return el movimiento registrado como DTO
     * @throws com.inventory.smart.exception.ResourceNotFoundException  si el producto no existe
     * @throws com.inventory.smart.exception.InsufficientStockException si es SALIDA y no hay stock suficiente
     */
    MovimientoResponse registrarMovimiento(MovimientoRequest request);

    /**
     * Recupera el historial completo de movimientos de un producto, ordenado
     * por fecha descendente (más recientes primero).
     *
     * @param productoId identificador del producto
     * @return lista de movimientos del producto como DTOs
     */
    List<MovimientoResponse> historialPorProducto(Long productoId);
}
