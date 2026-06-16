package com.inventory.smart.repository;

import com.inventory.smart.model.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementación en memoria de {@link CategoriaRepository} usando {@link GenericInMemoryRepository}.
 * <p>
 * Las operaciones básicas CRUD tienen complejidad O(1). La búsqueda por nombre
 * ({@code findByNombre}) tiene complejidad O(n) porque recorre todas las categorías.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Repository
public class InMemoryCategoriaRepository extends GenericInMemoryRepository<Categoria, Long>
        implements CategoriaRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryCategoriaRepository.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected Long extractId(Categoria entity) {
        return entity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Categoria withId(Categoria entity, Long id) {
        return new Categoria(id, entity.getNombre(), entity.getDescripcion());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(n). Busca una categoría cuyo nombre coincida exactamente.
     * </p>
     */
    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        log.debug("Buscando categoría por nombre='{}'", nombre);
        return store.values().stream()
                .filter(c -> c.getNombre().equals(nombre))
                .findFirst();
    }
}
