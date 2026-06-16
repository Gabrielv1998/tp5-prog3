package com.inventory.smart.service;

import com.inventory.smart.dto.ProductoRequest;
import com.inventory.smart.dto.ProductoResponse;
import com.inventory.smart.exception.ResourceNotFoundException;
import com.inventory.smart.model.Categoria;
import com.inventory.smart.model.Producto;
import com.inventory.smart.repository.CategoriaRepository;
import com.inventory.smart.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de productos.
 * <p>
 * Coordina las operaciones entre {@link ProductoRepository} y {@link CategoriaRepository},
 * aplicando reglas de negocio (validación de existencia de categorías, filtrado dinámico
 * con Stream API, ordenamiento con {@link Comparator}) y convirtiendo entidades a DTOs
 * mediante el método {@link #toResponse(Producto)}.
 * </p>
 * <p>
 * Utiliza inyección por constructor para cumplir con el Principio de Inversión de
 * Dependencias (DIP) de SOLID. Esta clase es <i>final</i> por decisión de diseño:
 * no está pensada para ser extendida.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    /** Repositorio de productos (in-memory, thread-safe). */
    private final ProductoRepository productoRepository;

    /** Repositorio de categorías para validar relaciones y desnormalizar nombres. */
    private final CategoriaRepository categoriaRepository;

    /**
     * Inyección por constructor de las dependencias del servicio.
     *
     * @param productoRepository  repositorio de productos
     * @param categoriaRepository repositorio de categorías
     */
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Aplica filtros en cadena usando Stream API:
     * <ol>
     *   <li>Filtro por nombre de categoría (si se proporciona)</li>
     *   <li>Filtro por precio mínimo (si se proporciona)</li>
     *   <li>Filtro por precio máximo (si se proporciona)</li>
     *   <li>Filtro por disponibilidad de stock (si se proporciona)</li>
     * </ol>
     * </p>
     */
    @Override
    public List<ProductoResponse> findAll(String categoria, Double precioMin, Double precioMax, Boolean enStock) {
        log.debug("findAll: categoria={}, precioMin={}, precioMax={}, enStock={}",
                categoria, precioMin, precioMax, enStock);

        var stream = productoRepository.findAll().stream();

        // Filtro por categoría
        if (categoria != null && !categoria.isBlank()) {
            var catOpt = categoriaRepository.findByNombre(categoria);
            if (catOpt.isPresent()) {
                Long catId = catOpt.get().getId();
                stream = stream.filter(p -> p.getCategoriaId().equals(catId));
            } else {
                // Si la categoría no existe, retornar lista vacía
                return List.of();
            }
        }

        // Filtro por precio mínimo
        if (precioMin != null) {
            stream = stream.filter(p -> p.getPrecio() >= precioMin);
        }

        // Filtro por precio máximo
        if (precioMax != null) {
            stream = stream.filter(p -> p.getPrecio() <= precioMax);
        }

        // Filtro por stock
        if (enStock != null && enStock) {
            stream = stream.filter(p -> p.getStock() > 0);
        }

        return stream.map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductoResponse findById(Long id) throws ResourceNotFoundException {
        log.debug("findById: id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return toResponse(producto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductoResponse create(ProductoRequest request) throws ResourceNotFoundException {
        log.debug("create: nombre='{}'", request.nombre());

        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria", request.categoriaId()));

        Producto producto = new Producto(
                null,
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stockInicial(),
                request.categoriaId()
        );

        Producto saved = productoRepository.save(producto);
        log.info("Producto creado: id={}, nombre='{}'", saved.getId(), saved.getNombre());
        return toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductoResponse update(Long id, ProductoRequest request) throws ResourceNotFoundException {
        log.debug("update: id={}", id);

        // Validar que el producto existe
        Producto existing = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        // Validar que la nueva categoría existe (si cambió)
        if (!existing.getCategoriaId().equals(request.categoriaId())) {
            if (!categoriaRepository.existsById(request.categoriaId())) {
                throw new ResourceNotFoundException("Categoria", request.categoriaId());
            }
        }

        // Crear nuevo producto con los datos actualizados (inmutabilidad)
        Producto updated = new Producto(
                id,
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stockInicial(),
                request.categoriaId()
        );

        Producto saved = productoRepository.save(updated);
        log.info("Producto actualizado: id={}, nombre='{}'", saved.getId(), saved.getNombre());
        return toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        log.debug("delete: id={}", id);
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productoRepository.deleteById(id);
        log.info("Producto eliminado: id={}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductoResponse> buscarPorNombre(String query) {
        log.debug("buscarPorNombre: query='{}'", query);
        return productoRepository.buscarPorNombre(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Soporta ordenamiento por {@code precio}, {@code stock} y {@code nombre}
     * en dirección ascendente ({@code asc}) o descendente ({@code desc}).
     * Si el campo u orden no son reconocidos, se retorna la lista sin ordenar.
     * </p>
     */
    @Override
    public List<ProductoResponse> findAllOrderedBy(String campo, String orden) {
        log.debug("findAllOrderedBy: campo={}, orden={}", campo, orden);

        List<Producto> productos = productoRepository.findAll();

        Comparator<Producto> comparator = switch (campo != null ? campo.toLowerCase() : "") {
            case "precio" -> Comparator.comparingDouble(Producto::getPrecio);
            case "stock" -> Comparator.comparingInt(Producto::getStock);
            case "nombre" -> Comparator.comparing(Producto::getNombre);
            default -> null;
        };

        if (comparator == null) {
            return productos.stream().map(this::toResponse).collect(Collectors.toList());
        }

        if ("desc".equalsIgnoreCase(orden)) {
            comparator = comparator.reversed();
        }

        return productos.stream()
                .sorted(comparator)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad {@link Producto} a su DTO de respuesta {@link ProductoResponse},
     * resolviendo el nombre de la categoría asociada.
     *
     * @param producto la entidad producto
     * @return el DTO con los datos del producto y el nombre de su categoría
     */
    private ProductoResponse toResponse(Producto producto) {
        String categoriaNombre = categoriaRepository.findById(producto.getCategoriaId())
                .map(Categoria::getNombre)
                .orElse("Desconocida");
        return ProductoResponse.fromProducto(producto, categoriaNombre);
    }
}
