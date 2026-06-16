package com.inventory.smart.repository;

import com.inventory.smart.model.MovimientoInventario;

import java.util.List;

/**
 * Interfaz de repositorio para la entidad {@link MovimientoInventario}.
 * <p>
 * Extiende {@link IGenericRepository} y agrega consulta de historial por producto.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface MovimientoRepository extends IGenericRepository<MovimientoInventario, Long> {

    /**
     * Recupera todos los movimientos asociados a un producto, ordenados por fecha descendente.
     *
     * @param productoId identificador del producto
     * @return lista de movimientos del producto (nunca {@code null}; vacía si no hay)
     */
    List<MovimientoInventario> findByProductoId(Long productoId);
}
