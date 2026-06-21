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
 *
 * <p>Esta clase abstracta proporciona:
 * <ul>
 *   <li>Almacenamiento en un {@code ConcurrentHashMap} con acceso concurrente seguro</li>
 *   <li>Generación automática de IDs mediante {@link AtomicLong}</li>
 *   <li>Implementaciones completas de todas las operaciones CRUD</li>
 * </ul>
 * Las subclases solo necesitan implementar {@link #extractId(Object)} y
 * {@link #withId(Object, Object)} para indicar cómo se obtiene y asigna el
 * identificador de la entidad.
 * </p>
 *
 * <p><b>¿Por qué {@code ConcurrentHashMap}?</b> Porque es thread-safe sin bloquear
 * todo el mapa: usa segmentación de locks (lock stripping) que permite a múltiples
 * hilos leer y escribir en buckets distintos de forma simultánea. Si se usara
 * {@code HashMap}, accesos concurrentes desde múltiples requests HTTP podrían
 * causar condiciones de carrera o {@code ConcurrentModificationException}.</p>
 *
 * @param <T>  tipo de la entidad gestionada
 * @param <ID> tipo del identificador único
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public abstract class GenericInMemoryRepository<T, ID> implements IGenericRepository<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(GenericInMemoryRepository.class);

    /**
     * Mapa concurrente que almacena las entidades.
     * <p>
     * Proporciona complejidad O(1) amortizado para {@code get}, {@code put} y
     * {@code remove}. El nombre {@code dataStore} sigue la convención del enunciado.
     * </p>
     */
    protected final ConcurrentHashMap<ID, T> dataStore = new ConcurrentHashMap<>();

    /**
     * Generador atómico de identificadores numéricos secuenciales.
     * <p>
     * Iniciado en 1 para que el primer ID sea 1. El uso de {@link AtomicLong}
     * garantiza que {@code getAndIncrement()} sea una operación atómica incluso
     * bajo acceso concurrente, sin necesidad de {@code synchronized}.
     * </p>
     */
    protected final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Extrae el identificador de una entidad.
     * <p>
     * Este método es invocado internamente en {@link #save(Object)} para determinar
     * si la entidad ya tiene ID asignado (actualización) o es nueva (inserción).
     * </p>
     *
     * @param entity la entidad de la cual extraer el identificador
     * @return el identificador único de la entidad, o {@code null} si aún no tiene
     */
    protected abstract ID extractId(T entity);

    /**
     * Crea una nueva instancia de la entidad con el identificador asignado.
     * <p>
     * Se usa para asignar el ID generado a entidades nuevas respetando la
     * inmutabilidad: en lugar de mutar la entidad, se crea una nueva copia
     * con el ID incluido.
     * </p>
     *
     * @param entity la entidad original sin ID
     * @param id     el identificador a asignar
     * @return una nueva instancia de la entidad con el ID asignado
     */
    protected abstract T withId(T entity, ID id);

    /**
     * Convierte el valor {@code long} del {@link AtomicLong} al tipo de ID concreto.
     * <p>
     * Por defecto asume que el ID es de tipo {@link Long}. Las subclases pueden
     * sobreescribir este método si usan otro tipo de identificador.
     * </p>
     *
     * @param value el valor numérico generado por {@link #idGenerator}
     * @return el identificador tipado
     */
    @SuppressWarnings("unchecked")
    protected ID toId(long value) {
        return (ID) Long.valueOf(value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Complejidad: O(n) — copia todas las entradas del mapa a una nueva lista.</p>
     *
     * @return lista con todas las entidades almacenadas
     */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Complejidad: O(1) amortizado — acceso por hash key.</p>
     *
     * @param id el identificador a buscar
     * @return {@code Optional} con la entidad si existe, vacío si no
     */
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Si la entidad no tiene ID (es decir, {@link #extractId} devuelve {@code null}),
     * se le asigna uno nuevo mediante {@link AtomicLong#getAndIncrement()}.
     * Si ya tiene ID, se sobreescribe la entrada existente (actualización).</p>
     *
     * <p>Complejidad: O(1) amortizado.</p>
     *
     * @param entity la entidad a guardar
     * @return la entidad guardada, con el ID asignado si era nueva
     */
    @Override
    public T save(T entity) {
        ID id = extractId(entity);
        if (id == null) {
            id = toId(idGenerator.getAndIncrement());
            entity = withId(entity, id);
        }
        dataStore.put(id, entity);
        log.debug("Entidad guardada: id={}, tipo={}", id, entity.getClass().getSimpleName());
        return entity;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Complejidad: O(1) amortizado.</p>
     *
     * @param id el identificador de la entidad a eliminar
     * @throws ResourceNotFoundException si no existe entidad con el ID indicado
     */
    @Override
    public void deleteById(ID id) {
        T removed = dataStore.remove(id);
        if (removed == null) {
            throw new ResourceNotFoundException("Entidad", id);
        }
        log.debug("Entidad eliminada: id={}", id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Complejidad: O(1) — usa {@link ConcurrentHashMap#containsKey}.</p>
     *
     * @param id el identificador a verificar
     * @return {@code true} si existe una entidad con ese ID
     */
    @Override
    public boolean existsById(ID id) {
        return dataStore.containsKey(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Complejidad: O(1) — usa {@link ConcurrentHashMap#size}.</p>
     *
     * @return el número de entidades almacenadas actualmente
     */
    @Override
    public long count() {
        return dataStore.size();
    }
}
