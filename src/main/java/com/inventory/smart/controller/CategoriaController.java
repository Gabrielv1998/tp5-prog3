package com.inventory.smart.controller;

import com.inventory.smart.dto.CategoriaRequest;
import com.inventory.smart.dto.CategoriaResponse;
import com.inventory.smart.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la gestión de categorías de productos.
 * <p>
 * Expone endpoints bajo {@code /api/categorias} para operaciones CRUD completas.
 * Delegación total de lógica de negocio al {@link CategoriaService}.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    /** Servicio de negocio para categorías. */
    private final CategoriaService categoriaService;

    /**
     * Inyección por constructor del servicio de categorías.
     *
     * @param categoriaService servicio de lógica de negocio
     */
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * Lista todas las categorías registradas.
     *
     * @return lista de categorías
     */
    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías registradas en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorías")
    })
    public ResponseEntity<List<CategoriaResponse>> findAll() {
        log.debug("GET /api/categorias");
        List<CategoriaResponse> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return la categoría encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por ID", description = "Obtiene una categoría por su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaResponse> findById(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id) {

        log.debug("GET /api/categorias/{}", id);
        CategoriaResponse categoria = categoriaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Crea una nueva categoría.
     *
     * @param request datos de la categoría
     * @return la categoría creada con HTTP 201
     */
    @PostMapping
    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<CategoriaResponse> create(
            @Parameter(description = "Datos de la categoría", required = true)
            @Valid @RequestBody CategoriaRequest request) {

        log.debug("POST /api/categorias: nombre='{}'", request.nombre());
        CategoriaResponse created = categoriaService.create(request);
        return ResponseEntity
                .created(URI.create("/api/categorias/" + created.id()))
                .body(created);
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id      identificador de la categoría
     * @param request nuevos datos
     * @return la categoría actualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<CategoriaResponse> update(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la categoría", required = true)
            @Valid @RequestBody CategoriaRequest request) {

        log.debug("PUT /api/categorias/{}", id);
        CategoriaResponse updated = categoriaService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina una categoría.
     * <p>
     * <b>Nota:</b> no se puede eliminar una categoría que tiene productos asociados.
     * </p>
     *
     * @param id identificador de la categoría
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema. No se permite si tiene productos asociados")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
            @ApiResponse(responseCode = "400", description = "La categoría tiene productos asociados"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la categoría a eliminar", required = true)
            @PathVariable Long id) {

        log.debug("DELETE /api/categorias/{}", id);
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
