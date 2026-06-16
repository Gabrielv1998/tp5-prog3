package com.inventory.smart.service;

import com.inventory.smart.dto.ProductoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la generación de reportes de rendimiento de las operaciones del sistema.
 * <p>
 * Mide el tiempo de ejecución de las operaciones principales usando {@link System#nanoTime()}
 * y genera un reporte estructurado. Este servicio tiene un propósito educativo: permite a los
 * estudiantes analizar el comportamiento temporal de las distintas operaciones bajo diferentes
 * volúmenes de datos y visualizar los órdenes de complejidad algorítmica.
 * </p>
 * <p>
 * <b>Fase de warmup:</b> se ejecutan 100 iteraciones de calentamiento antes de medir,
 * para que la JVM optimice (JIT) y los resultados sean estables. Luego se realizan
 * {@code ITERACIONES_MEDICION} ejecuciones cronometradas y se calcula el promedio.
 * </p>
 * <p>
 * <b>Órdenes de complejidad (Big O) para cada operación medida:</b>
 * <table>
 *   <tr><th>Operación</th><th>Big O</th><th>Explicación</th></tr>
 *   <tr><td>findById</td><td>O(1)</td><td>Acceso directo al ConcurrentHashMap</td></tr>
 *   <tr><td>findAll</td><td>O(n)</td><td>Iteración sobre todas las entradas</td></tr>
 *   <tr><td>findAll con filtros</td><td>O(n)</td><td>Stream con filtros encadenados</td></tr>
 *   <tr><td>buscarPorNombre</td><td>O(n)</td><td>Stream.filter() sobre todos los productos</td></tr>
 *   <tr><td>findAllOrderedBy</td><td>O(n log n)</td><td>Ordenamiento con Comparator</td></tr>
 *   <tr><td>create</td><td>O(1)</td><td>Inserción directa en el mapa</td></tr>
 * </table>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Service
public class PerformanceReportService {

    private static final Logger log = LoggerFactory.getLogger(PerformanceReportService.class);

    /** Iteraciones de calentamiento antes de medir. */
    private static final int WARMUP_ITERATIONS = 100;

    /** Iteraciones cronometradas para calcular el promedio. */
    private static final int MEASURE_ITERATIONS = 50;

    private final ProductoService productoService;

    /**
     * Inyección por constructor de los servicios a medir.
     *
     * @param productoService servicio de productos
     */
    public PerformanceReportService(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Genera un reporte completo de rendimiento midiendo las operaciones principales
     * del sistema sobre el volumen actual de datos.
     * <p>
     * El reporte incluye:
     * <ul>
     *   <li>Cantidad total de productos en el sistema</li>
     *   <li>Tiempos promedio (en microsegundos) para cada operación</li>
     *   <li>Big O teórico de cada operación</li>
     * </ul>
     * </p>
     *
     * @return un mapa estructurado con las métricas de rendimiento
     */
    public Map<String, Object> generarReporte() {
        log.info("=== Generando reporte de rendimiento ===");

        // Obtener todos los productos para tener una base de medición
        List<ProductoResponse> todos = productoService.findAll(null, null, null, null);
        long totalProductos = todos.size();
        log.info("Total de productos en el sistema: {}", totalProductos);

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("titulo", "Reporte de Rendimiento - Smart Inventory");
        reporte.put("totalProductos", totalProductos);
        reporte.put("iteracionesWarmup", WARMUP_ITERATIONS);
        reporte.put("iteracionesMedicion", MEASURE_ITERATIONS);

        // Mediciones
        Map<String, Object> mediciones = new LinkedHashMap<>();

        // 1. findById (O(1))
        if (!todos.isEmpty()) {
            Long sampleId = todos.get(0).id();
            mediciones.put("findById", medirOperacion(
                    "findById (O(1))",
                    () -> productoService.findById(sampleId)
            ));
        }

        // 2. findAll sin filtros (O(n))
        mediciones.put("findAll_sinFiltros", medirOperacion(
                "findAll sin filtros (O(n))",
                () -> productoService.findAll(null, null, null, null)
        ));

        // 3. findAll con filtro de categoría (O(n))
        if (!todos.isEmpty()) {
            String catNombre = todos.get(0).categoriaNombre();
            mediciones.put("findAll_filtroCategoria", medirOperacion(
                    "findAll con filtro categoria (O(n))",
                    () -> productoService.findAll(catNombre, null, null, null)
            ));
        }

        // 4. findAll con filtro de precio (O(n))
        mediciones.put("findAll_filtroPrecio", medirOperacion(
                "findAll con filtro precio (O(n))",
                () -> productoService.findAll(null, 10.0, 1000.0, null)
        ));

        // 5. buscarPorNombre (O(n))
        mediciones.put("buscarPorNombre", medirOperacion(
                "buscarPorNombre (O(n))",
                () -> productoService.buscarPorNombre("a")
        ));

        // 6. findAllOrderedBy (O(n log n))
        mediciones.put("findAllOrderedBy_precioAsc", medirOperacion(
                "findAllOrderedBy precio asc (O(n log n))",
                () -> productoService.findAllOrderedBy("precio", "asc")
        ));

        mediciones.put("findAllOrderedBy_precioDesc", medirOperacion(
                "findAllOrderedBy precio desc (O(n log n))",
                () -> productoService.findAllOrderedBy("precio", "desc")
        ));

        reporte.put("mediciones", mediciones);

        // Complejidades teóricas
        Map<String, String> complejidades = new LinkedHashMap<>();
        complejidades.put("findById", "O(1) - Acceso directo al ConcurrentHashMap");
        complejidades.put("findAll", "O(n) - Iteración secuencial");
        complejidades.put("findAll con filtros", "O(n) - Stream API con filtros encadenados");
        complejidades.put("buscarPorNombre", "O(n) - Filtro sobre todos los productos");
        complejidades.put("findAllOrderedBy", "O(n log n) - Ordenamiento con Comparator");
        complejidades.put("create", "O(1) - Inserción directa en ConcurrentHashMap");
        complejidades.put("update", "O(1) - Reemplazo directo en ConcurrentHashMap");
        complejidades.put("delete", "O(1) - Eliminación directa del ConcurrentHashMap");
        reporte.put("complejidadesTeoricas", complejidades);

        log.info("=== Reporte de rendimiento generado exitosamente ===");
        return reporte;
    }

    /**
     * Mide el tiempo promedio de ejecución de una operación.
     * <p>
     * Ejecuta {@link #WARMUP_ITERATIONS} iteraciones de calentamiento sin medir,
     * luego {@link #MEASURE_ITERATIONS} iteraciones cronometradas y calcula el
     * promedio en microsegundos (μs).
     * </p>
     *
     * @param nombre    nombre descriptivo de la operación (para logging)
     * @param operacion la operación a medir como {@link Runnable}
     * @return un mapa con el nombre de la operación y el tiempo promedio en μs
     */
    private Map<String, Object> medirOperacion(String nombre, Runnable operacion) {
        // Fase de warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            operacion.run();
        }

        // Fase de medición
        long tiempoTotal = 0;
        for (int i = 0; i < MEASURE_ITERATIONS; i++) {
            long inicio = System.nanoTime();
            operacion.run();
            long fin = System.nanoTime();
            tiempoTotal += (fin - inicio);
        }

        double promedioNs = (double) tiempoTotal / MEASURE_ITERATIONS;
        double promedioUs = promedioNs / 1_000.0; // Convertir nanosegundos a microsegundos

        log.debug("{}: {} μs (promedio de {} iteraciones)", nombre, String.format("%.3f", promedioUs), MEASURE_ITERATIONS);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("operacion", nombre);
        resultado.put("promedioMicrosegundos", Math.round(promedioUs * 1000.0) / 1000.0);
        resultado.put("iteraciones", MEASURE_ITERATIONS);
        return resultado;
    }
}
