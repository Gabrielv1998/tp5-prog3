package com.inventory.smart.repository;

import com.inventory.smart.model.MovimientoInventario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación en memoria de {@link MovimientoRepository} usando {@link GenericInMemoryRepository}.
 * <p>
 * Las operaciones básicas CRUD tienen complejidad O(1). La consulta de historial
 * por producto ({@code findByProductoId}) tiene complejidad O(n) y retorna los
 * resultados ordenados por fecha descendente (más recientes primero).
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Repository
public class InMemoryMovimientoRepository extends GenericInMemoryRepository<MovimientoInventario, Long>
        implements MovimientoRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMovimientoRepository.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected Long extractId(MovimientoInventario entity) {
        return entity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MovimientoInventario withId(MovimientoInventario entity, Long id) {
        return new MovimientoInventario(id, entity.getProductoId(), entity.getTipo(),
                entity.getCantidad(), entity.getFecha(), entity.getMotivo());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(n log n) debido al ordenamiento por fecha descendente.
     * Filtra los movimientos por {@code productoId} y los ordena de más reciente a más antiguo.
     * </p>
     */
    @Override
    public List<MovimientoInventario> findByProductoId(Long productoId) {
        log.debug("Buscando movimientos por productoId={}", productoId);
        return store.values().stream()
                .filter(m -> m.getProductoId().equals(productoId))
                .sorted(Comparator.comparing(MovimientoInventario::getFecha).reversed())
                .collect(Collectors.toList());
    }
}
