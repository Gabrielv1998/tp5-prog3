package com.inventory.smart.controller;

import com.inventory.smart.dto.MovimientoRequest;
import com.inventory.smart.dto.MovimientoResponse;
import com.inventory.smart.service.MovimientoService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para el registro y consulta de movimientos de inventario.
 * <p>
 * Expone endpoints bajo {@code /api/movimientos} para registrar entradas/salidas
 * y consultar el historial de movimientos de un producto.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@RestController
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos", description = "Registro y consulta de movimientos de inventario")
public class MovimientoController {

    private static final Logger log = LoggerFactory.getLogger(MovimientoController.class);

    /** Servicio de negocio para movimientos. */
    private final MovimientoService movimientoService;

    /**
     * Inyección por constructor del servicio de movimientos.
     *
     * @param movimientoService servicio de lógica de negocio
     */
    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /**
     * Registra un nuevo movimiento de inventario (entrada o salida).
     *
     * @param request datos del movimiento
     * @return el movimiento registrado con HTTP 201
     */
    @PostMapping
    @Operation(summary = "Registrar movimiento", description = "Registra una entrada o salida de inventario. Actualiza el stock del producto atómicamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente para la salida"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MovimientoResponse> registrarMovimiento(
            @Parameter(description = "Datos del movimiento", required = true)
            @Valid @RequestBody MovimientoRequest request) {

        log.debug("POST /api/movimientos: productoId={}, tipo={}, cantidad={}",
                request.productoId(), request.tipo(), request.cantidad());
        MovimientoResponse response = movimientoService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta el historial de movimientos de un producto.
     *
     * @param productoId identificador del producto
     * @return lista de movimientos ordenados por fecha descendente
     */
    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Historial de movimientos", description = "Obtiene el historial completo de movimientos de un producto, ordenado por fecha descendente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    })
    public ResponseEntity<List<MovimientoResponse>> historialPorProducto(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long productoId) {

        log.debug("GET /api/movimientos/producto/{}", productoId);
        List<MovimientoResponse> historial = movimientoService.historialPorProducto(productoId);
        return ResponseEntity.ok(historial);
    }
}
