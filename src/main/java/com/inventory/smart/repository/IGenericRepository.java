package com.inventory.smart.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica que define las operaciones CRUD básicas para cualquier repositorio.
 * <p>
 * Proporciona un contrato uniforme para todos los repositorios del sistema,
 * independientemente de la estrategia de persistencia utilizada.
 * </p>
 *
 * @param <T>  tipo de la entidad gestionada por el repositorio
 * @param <ID> tipo del identificador único de la entidad
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface IGenericRepository<T, ID> {

    /**
     * Recupera todas las entidades del repositorio.
     *
     * @return lista de todas las entidades (nunca {@code null}; vacía si no hay datos)
     */
    List<T> findAll();

    /**
     * Busca una entidad por su identificador único.
     *
     * @param id el identificador de la entidad (no debe ser {@code null})
     * @return un {@link Optional} que contiene la entidad si existe, o vacío en caso contrario
     */
    Optional<T> findById(ID id);

    /**
     * Persiste o actualiza una entidad.
     * <p>
     * Si la entidad no tiene identificador asignado, se le asigna uno nuevo.
     * Si ya existe una entidad con el mismo identificador, se sobrescribe.
     * </p>
     *
     * @param entity la entidad a guardar (no debe ser {@code null})
     * @return la entidad guardada con su identificador asignado
     */
    T save(T entity);

    /**
     * Elimina una entidad por su identificador.
     *
     * @param id el identificador de la entidad a eliminar
     * @throws com.inventory.smart.exception.ResourceNotFoundException si la entidad no existe
     */
    void deleteById(ID id);

    /**
     * Verifica si existe una entidad con el identificador dado.
     *
     * @param id el identificador a verificar
     * @return {@code true} si la entidad existe, {@code false} en caso contrario
     */
    boolean existsById(ID id);

    /**
     * Cuenta el número total de entidades en el repositorio.
     *
     * @return la cantidad de entidades almacenadas
     */
    long count();
}
