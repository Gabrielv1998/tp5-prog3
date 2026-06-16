package com.inventory.smart.repository;

import com.inventory.smart.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación en memoria de {@link ProductoRepository} usando {@link GenericInMemoryRepository}.
 * <p>
 * Almacena los productos en un {@link java.util.concurrent.ConcurrentHashMap} heredado
 * de la clase base. Las consultas por flujo de datos ({@code findByCategoriaId},
 * {@code buscarPorNombre}) tienen complejidad O(n) porque recorren todas las entidades
 * aplicando filtros con {@code stream().filter()}.
 * </p>
 * <p>
 * Las operaciones básicas (findById, save, deleteById, existsById) heredadas de
 * {@code GenericInMemoryRepository} tienen complejidad O(1) por usar acceso directo
 * al mapa.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Repository
public class InMemoryProductoRepository extends GenericInMemoryRepository<Producto, Long>
        implements ProductoRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryProductoRepository.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected Long extractId(Producto entity) {
        return entity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Producto withId(Producto entity, Long id) {
        return new Producto(id, entity.getNombre(), entity.getDescripcion(),
                entity.getPrecio(), entity.getStock(), entity.getCategoriaId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(n). Recorre todos los productos y filtra por {@code categoriaId}.
     * </p>
     */
    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        log.debug("Buscando productos por categoriaId={}", categoriaId);
        return store.values().stream()
                .filter(p -> p.getCategoriaId().equals(categoriaId))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(n). Recorre todos los productos y filtra aquellos cuyo nombre
     * contiene el texto de búsqueda (case-insensitive).
     * </p>
     */
    @Override
    public List<Producto> buscarPorNombre(String query) {
        log.debug("Buscando productos por nombre que contenga '{}'", query);
        String lowerQuery = query.toLowerCase();
        return store.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}
