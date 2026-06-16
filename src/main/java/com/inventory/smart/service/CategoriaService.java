package com.inventory.smart.service;

import com.inventory.smart.dto.CategoriaRequest;
import com.inventory.smart.dto.CategoriaResponse;
import com.inventory.smart.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Servicio de negocio para la gestión de categorías.
 * <p>
 * Define las operaciones CRUD para categorías de productos.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface CategoriaService {

    /**
     * Lista todas las categorías registradas.
     *
     * @return lista de categorías como DTOs
     */
    List<CategoriaResponse> findAll();

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return la categoría como DTO
     * @throws ResourceNotFoundException si no existe
     */
    CategoriaResponse findById(Long id) throws ResourceNotFoundException;

    /**
     * Crea una nueva categoría.
     *
     * @param request datos de la categoría
     * @return la categoría creada como DTO
     */
    CategoriaResponse create(CategoriaRequest request);

    /**
     * Actualiza una categoría existente.
     *
     * @param id      identificador de la categoría
     * @param request nuevos datos
     * @return la categoría actualizada como DTO
     * @throws ResourceNotFoundException si no existe
     */
    CategoriaResponse update(Long id, CategoriaRequest request) throws ResourceNotFoundException;

    /**
     * Elimina una categoría.
     * <p>
     * <b>Regla de negocio:</b> no se puede eliminar una categoría que tiene productos asociados.
     * </p>
     *
     * @param id identificador de la categoría
     * @throws ResourceNotFoundException si no existe
     * @throws com.inventory.smart.exception.BusinessRuleException si tiene productos asociados
     */
    void delete(Long id) throws ResourceNotFoundException;
}
