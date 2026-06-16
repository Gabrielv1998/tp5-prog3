package com.inventory.smart.service;

import com.inventory.smart.dto.CategoriaRequest;
import com.inventory.smart.dto.CategoriaResponse;
import com.inventory.smart.exception.BusinessRuleException;
import com.inventory.smart.exception.ResourceNotFoundException;
import com.inventory.smart.model.Categoria;
import com.inventory.smart.repository.CategoriaRepository;
import com.inventory.smart.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de categorías.
 * <p>
 * Coordina las operaciones entre {@link CategoriaRepository} y {@link ProductoRepository},
 * aplicando la regla de negocio que impide eliminar categorías con productos asociados.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    /** Repositorio de categorías. */
    private final CategoriaRepository categoriaRepository;

    /** Repositorio de productos (para validar regla de negocio en eliminación). */
    private final ProductoRepository productoRepository;

    /**
     * Inyección por constructor de las dependencias del servicio.
     *
     * @param categoriaRepository repositorio de categorías
     * @param productoRepository  repositorio de productos
     */
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
                                ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoriaResponse> findAll() {
        log.debug("findAll categorías");
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::fromCategoria)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoriaResponse findById(Long id) throws ResourceNotFoundException {
        log.debug("findById: id={}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        return CategoriaResponse.fromCategoria(categoria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoriaResponse create(CategoriaRequest request) {
        log.debug("create: nombre='{}'", request.nombre());
        Categoria categoria = new Categoria(null, request.nombre(), request.descripcion());
        Categoria saved = categoriaRepository.save(categoria);
        log.info("Categoría creada: id={}, nombre='{}'", saved.getId(), saved.getNombre());
        return CategoriaResponse.fromCategoria(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoriaResponse update(Long id, CategoriaRequest request) throws ResourceNotFoundException {
        log.debug("update: id={}", id);
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        Categoria updated = new Categoria(id, request.nombre(), request.descripcion());
        Categoria saved = categoriaRepository.save(updated);
        log.info("Categoría actualizada: id={}, nombre='{}'", saved.getId(), saved.getNombre());
        return CategoriaResponse.fromCategoria(saved);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Regla de negocio aplicada:</b> verifica que no existan productos asociados
     * a esta categoría antes de eliminarla. Si hay productos, lanza
     * {@link BusinessRuleException} con HTTP 400.
     * </p>
     */
    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        log.debug("delete: id={}", id);

        // Validar existencia
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", id);
        }

        // Validar regla de negocio: no eliminar categorías con productos
        int productosAsociados = productoRepository.findByCategoriaId(id).size();
        if (productosAsociados > 0) {
            throw new BusinessRuleException(
                    "No se puede eliminar la categoría porque tiene " + productosAsociados
                            + " producto(s) asociado(s). Reasigne o elimine los productos primero.");
        }

        categoriaRepository.deleteById(id);
        log.info("Categoría eliminada: id={}", id);
    }
}
