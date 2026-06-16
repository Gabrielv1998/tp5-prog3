package com.inventory.smart.controller;

import com.inventory.smart.dto.ProductoRequest;
import com.inventory.smart.dto.ProductoResponse;
import com.inventory.smart.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la gestión de productos del inventario.
 * <p>
 * Expone endpoints bajo {@code /api/productos} para las operaciones CRUD,
 * búsqueda por nombre, ordenamiento y listado con filtros dinámicos.
 * Este controlador es <i>thin</i>: delega toda la lógica de negocio al
 * {@link ProductoService} y solo gestiona el mapeo HTTP.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos del inventario")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    /** Servicio de negocio para productos (inyectado por constructor). */
    private final ProductoService productoService;

    /**
     * Inyección por constructor del servicio de productos.
     *
     * @param productoService servicio de lógica de negocio
     */
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Lista productos con filtros opcionales.
     *
     * @param categoria nombre de categoría (opcional)
     * @param precioMin precio mínimo (opcional)
     * @param precioMax precio máximo (opcional)
     * @param enStock   solo productos con stock (opcional)
     * @return lista de productos que cumplen los criterios
     */
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos con filtros opcionales por categoría, rango de precio y disponibilidad de stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    public ResponseEntity<List<ProductoResponse>> findAll(
            @Parameter(description = "Filtrar por nombre de categoría")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Precio mínimo")
            @RequestParam(required = false) Double precioMin,
            @Parameter(description = "Precio máximo")
            @RequestParam(required = false) Double precioMax,
            @Parameter(description = "Filtrar solo productos con stock > 0")
            @RequestParam(required = false) Boolean enStock) {

        log.debug("GET /api/productos: categoria={}, precioMin={}, precioMax={}, enStock={}",
                categoria, precioMin, precioMax, enStock);
        List<ProductoResponse> productos = productoService.findAll(categoria, precioMin, precioMax, enStock);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto por su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> findById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id) {

        log.debug("GET /api/productos/{}", id);
        ProductoResponse producto = productoService.findById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Crea un nuevo producto.
     *
     * @param request datos del producto a crear (validados con Jakarta Bean Validation)
     * @return el producto creado con HTTP 201 y la URI del recurso
     */
    @PostMapping
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o categoría inexistente")
    })
    public ResponseEntity<ProductoResponse> create(
            @Parameter(description = "Datos del producto", required = true)
            @Valid @RequestBody ProductoRequest request) {

        log.debug("POST /api/productos: nombre='{}'", request.nombre());
        ProductoResponse created = productoService.create(request);
        return ResponseEntity
                .created(URI.create("/api/productos/" + created.id()))
                .body(created);
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id      identificador del producto
     * @param request nuevos datos del producto
     * @return el producto actualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza todos los datos de un producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrados"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductoResponse> update(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del producto", required = true)
            @Valid @RequestBody ProductoRequest request) {

        log.debug("PUT /api/productos/{}", id);
        ProductoResponse updated = productoService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long id) {

        log.debug("DELETE /api/productos/{}", id);
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca productos por nombre (búsqueda parcial, case-insensitive).
     *
     * @param q texto a buscar en el nombre del producto
     * @return lista de productos que coinciden
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos cuyo nombre contenga el texto indicado (case-insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda")
    })
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(
            @Parameter(description = "Texto de búsqueda", required = true)
            @RequestParam("q") String q) {

        log.debug("GET /api/productos/buscar?q={}", q);
        List<ProductoResponse> resultados = productoService.buscarPorNombre(q);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Lista productos ordenados por un campo y dirección específicos.
     *
     * @param campo campo de ordenamiento: "precio", "stock" o "nombre" (default: "nombre")
     * @param orden dirección: "asc" o "desc" (default: "asc")
     * @return lista ordenada de productos
     */
    @GetMapping("/ordenados")
    @Operation(summary = "Listar productos ordenados", description = "Obtiene todos los productos ordenados por el campo y dirección especificados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista ordenada de productos")
    })
    public ResponseEntity<List<ProductoResponse>> findAllOrderedBy(
            @Parameter(description = "Campo de ordenamiento: precio, stock, nombre")
            @RequestParam(defaultValue = "nombre") String campo,
            @Parameter(description = "Dirección del ordenamiento: asc o desc")
            @RequestParam(defaultValue = "asc") String orden) {

        log.debug("GET /api/productos/ordenados?campo={}&orden={}", campo, orden);
        List<ProductoResponse> productos = productoService.findAllOrderedBy(campo, orden);
        return ResponseEntity.ok(productos);
    }
}
