package com.inventory.smart.repository;

import com.inventory.smart.model.Producto;

import java.util.List;

/**
 * Interfaz de repositorio para la entidad {@link Producto}.
 * <p>
 * Extiende {@link IGenericRepository} con operaciones CRUD genéricas y agrega
 * consultas específicas de producto: búsqueda por categoría y búsqueda por nombre.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface ProductoRepository extends IGenericRepository<Producto, Long> {

    /**
     * Busca todos los productos que pertenecen a una categoría específica.
     *
     * @param categoriaId identificador de la categoría
     * @return lista de productos de la categoría (nunca {@code null}; vacía si no hay)
     */
    List<Producto> findByCategoriaId(Long categoriaId);

    /**
     * Busca productos cuyo nombre contenga el texto de búsqueda (case-insensitive).
     *
     * @param query texto a buscar en el nombre del producto
     * @return lista de productos que coinciden con la búsqueda
     */
    List<Producto> buscarPorNombre(String query);
}
