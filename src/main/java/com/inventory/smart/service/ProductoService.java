package com.inventory.smart.service;

import com.inventory.smart.dto.ProductoRequest;
import com.inventory.smart.dto.ProductoResponse;
import com.inventory.smart.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Servicio de negocio para la gestión de productos.
 * <p>
 * Define las operaciones de CRUD, búsqueda, filtrado y ordenamiento
 * aplicables a los productos del inventario.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface ProductoService {

    /**
     * Lista productos aplicando filtros opcionales.
     *
     * @param categoria nombre de categoría para filtrar (puede ser {@code null})
     * @param precioMin precio mínimo para filtrar (puede ser {@code null})
     * @param precioMax precio máximo para filtrar (puede ser {@code null})
     * @param enStock   {@code true} para solo productos con stock > 0 (puede ser {@code null})
     * @return lista de productos que cumplen los filtros como DTOs
     */
    List<ProductoResponse> findAll(String categoria, Double precioMin, Double precioMax, Boolean enStock);

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto como DTO
     * @throws ResourceNotFoundException si el producto no existe
     */
    ProductoResponse findById(Long id) throws ResourceNotFoundException;

    /**
     * Crea un nuevo producto a partir de los datos de la petición.
     *
     * @param request datos del producto a crear
     * @return el producto creado como DTO
     * @throws ResourceNotFoundException si la categoría asociada no existe
     */
    ProductoResponse create(ProductoRequest request) throws ResourceNotFoundException;

    /**
     * Actualiza un producto existente.
     *
     * @param id      identificador del producto a actualizar
     * @param request nuevos datos del producto
     * @return el producto actualizado como DTO
     * @throws ResourceNotFoundException si el producto o la categoría no existen
     */
    ProductoResponse update(Long id, ProductoRequest request) throws ResourceNotFoundException;

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto a eliminar
     * @throws ResourceNotFoundException si el producto no existe
     */
    void delete(Long id) throws ResourceNotFoundException;

    /**
     * Busca productos cuyo nombre contenga el texto indicado (case-insensitive).
     *
     * @param query texto de búsqueda
     * @return lista de productos que coinciden
     */
    List<ProductoResponse> buscarPorNombre(String query);

    /**
     * Lista todos los productos ordenados por el campo y dirección especificados.
     *
     * @param campo campo por el cual ordenar: "precio", "stock" o "nombre"
     * @param orden dirección: "asc" o "desc"
     * @return lista ordenada de productos como DTOs
     */
    List<ProductoResponse> findAllOrderedBy(String campo, String orden);
}
