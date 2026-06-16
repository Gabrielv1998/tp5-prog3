package com.inventory.smart.controller;

import com.inventory.smart.dto.AlertaStockResponse;
import com.inventory.smart.service.AlertaService;
import com.inventory.smart.service.PerformanceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el monitoreo de alertas de stock y reportes administrativos.
 * <p>
 * Expone endpoints bajo {@code /api/alertas} para detectar productos con stock bajo
 * y bajo {@code /api/admin} para reportes de rendimiento.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Alertas y Administración", description = "Monitoreo de stock bajo y reportes de rendimiento")
public class AlertaController {

    private static final Logger log = LoggerFactory.getLogger(AlertaController.class);

    /** Servicio de detección de alertas. */
    private final AlertaService alertaService;

    /** Servicio de reportes de rendimiento. */
    private final PerformanceReportService performanceReportService;

    /**
     * Inyección por constructor de los servicios requeridos.
     *
     * @param alertaService           servicio de alertas de stock
     * @param performanceReportService servicio de reportes de rendimiento
     */
    public AlertaController(AlertaService alertaService,
                            PerformanceReportService performanceReportService) {
        this.alertaService = alertaService;
        this.performanceReportService = performanceReportService;
    }

    /**
     * Detecta todos los productos con stock por debajo del umbral mínimo.
     * <p>
     * Retorna una lista de alertas indicando el nivel: BAJO o CRITICO.
     * </p>
     *
     * @return lista de alertas de stock
     */
    @GetMapping("/alertas/stock-bajo")
    @Operation(summary = "Detectar stock bajo", description = "Obtiene todos los productos cuyo stock está por debajo del umbral mínimo configurado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de alertas de stock bajo")
    })
    public ResponseEntity<List<AlertaStockResponse>> detectarStockBajo() {
        log.debug("GET /api/alertas/stock-bajo");
        List<AlertaStockResponse> alertas = alertaService.detectarStockBajo();
        return ResponseEntity.ok(alertas);
    }

    /**
     * Genera un reporte de rendimiento de las operaciones del sistema.
     * <p>
     * El reporte incluye tiempos promedio de ejecución para cada operación
     * principal, junto con las complejidades algorítmicas teóricas (Big O).
     * Útil con fines educativos y de diagnóstico.
     * </p>
     *
     * @return reporte de rendimiento estructurado en formato JSON
     */
    @GetMapping("/admin/performance-report")
    @Operation(summary = "Reporte de rendimiento", description = "Genera un reporte de rendimiento con tiempos promedio de las operaciones y complejidades Big O")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    })
    public ResponseEntity<Map<String, Object>> performanceReport() {
        log.debug("GET /api/admin/performance-report");
        Map<String, Object> reporte = performanceReportService.generarReporte();
        return ResponseEntity.ok(reporte);
    }
}
