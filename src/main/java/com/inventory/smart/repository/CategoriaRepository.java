package com.inventory.smart.repository;

import com.inventory.smart.model.Categoria;

import java.util.Optional;

/**
 * Interfaz de repositorio para la entidad {@link Categoria}.
 * <p>
 * Extiende {@link IGenericRepository} y agrega búsqueda por nombre exacto.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface CategoriaRepository extends IGenericRepository<Categoria, Long> {

    /**
     * Busca una categoría por su nombre exacto (case-sensitive).
     *
     * @param nombre el nombre exacto de la categoría
     * @return un {@link Optional} con la categoría si existe
     */
    Optional<Categoria> findByNombre(String nombre);
}
