package com.inventory.smart.repository;

import com.inventory.smart.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación genérica en memoria de {@link IGenericRepository} usando
 * {@link ConcurrentHashMap} para operaciones thread-safe de complejidad O(1).
 * <p>
 * Esta clase abstracta proporciona:
 * <ul>
 *   <li>Almacenamiento en un {@code ConcurrentHashMap} con acceso concurrente seguro</li>
 *   <li>Generación automática de IDs mediante {@link AtomicLong}</li>
 *   <li>Implementaciones completas de todas las operaciones CRUD</li>
 * </ul>
 * Las subclases solo necesitan implementar {@link #extractId(Object)} para indicar
 * cómo se obtiene el identificador de la entidad.
 * </p>
 *
 * @param <T>  tipo de la entidad gestionada
 * @param <ID> tipo del identificador único
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public abstract class GenericInMemoryRepository<T, ID> implements IGenericRepository<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(GenericInMemoryRepository.class);

    /** Mapa concurrente que almacena las entidades, con complejidad O(1) para get/put. */
    protected final ConcurrentHashMap<ID, T> store = new ConcurrentHashMap<>();

    /** Generador atómico de identificadores numéricos secuenciales. */
    protected final AtomicLong nextId = new AtomicLong(1);

    /**
     * Extrae el identificador de una entidad.
     * <p>
     * Este método es invocado internamente para operaciones como {@link #save(Object)}
     * y debe ser implementado por cada repositorio concreto.
     * </p>
     *
     * @param entity la entidad de la cual extraer el identificador
     * @return el identificador único de la entidad
     */
    protected abstract ID extractId(T entity);

    /**
     * Crea una nueva instancia de la entidad con el identificador asignado.
     * <p>
     * Método de conveniencia para que las subclases puedan construir entidades
     * con el ID generado automáticamente.
     * </p>
     *
     * @param entity la entidad original
     * @param id     el nuevo identificador a asignar
     * @return una nueva instancia de la entidad con el ID asignado
     */
    protected abstract T withId(T entity, ID id);

    /**
     * Convierte el {@link AtomicLong} al tipo de identificador concreto.
     * <p>
     * Por defecto asume que el ID es de tipo {@link Long}. Las subclases
     * deben sobreescribir este método si usan otro tipo de identificador.
     * </p>
     *
     * @param value el valor numérico generado
     * @return el identificador tipado
     */
    @SuppressWarnings("unchecked")
    protected ID toId(long value) {
        return (ID) Long.valueOf(value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(n) donde n es el número de entidades.
     * </p>
     */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(1).
     * </p>
     */
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Si la entidad no tiene ID (es decir, {@code extractId} devuelve {@code null}),
     * se le asigna uno nuevo generado atómicamente. Complejidad: O(1).
     * </p>
     */
    @Override
    public T save(T entity) {
        ID id = extractId(entity);
        if (id == null) {
            id = toId(nextId.getAndIncrement());
            entity = withId(entity, id);
        }
        store.put(id, entity);
        log.debug("Entidad guardada: id={}, tipo={}", id, entity.getClass().getSimpleName());
        return entity;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(1).
     * </p>
     *
     * @throws ResourceNotFoundException si no existe entidad con el ID indicado
     */
    @Override
    public void deleteById(ID id) {
        T removed = store.remove(id);
        if (removed == null) {
            throw new ResourceNotFoundException("Entidad", id);
        }
        log.debug("Entidad eliminada: id={}", id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(1).
     * </p>
     */
    @Override
    public boolean existsById(ID id) {
        return store.containsKey(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Complejidad: O(1).
     * </p>
     */
    @Override
    public long count() {
        return store.size();
    }
}
