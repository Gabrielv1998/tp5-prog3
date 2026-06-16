package com.inventory.smart.service;

import com.inventory.smart.config.StockConfig;
import com.inventory.smart.dto.AlertaStockResponse;
import com.inventory.smart.model.Categoria;
import com.inventory.smart.model.NivelAlerta;
import com.inventory.smart.model.Producto;
import com.inventory.smart.repository.CategoriaRepository;
import com.inventory.smart.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de detección de alertas de stock.
 * <p>
 * Utiliza los umbrales definidos en {@link StockConfig} para evaluar el nivel
 * de stock de cada producto. Filtra aquellos con stock por debajo del mínimo
 * y emite una {@link AlertaStockResponse} con el nivel correspondiente.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Service
public class AlertaServiceImpl implements AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaServiceImpl.class);

    /** Repositorio de productos. */
    private final ProductoRepository productoRepository;

    /** Repositorio de categorías (para obtener nombre de producto en la alerta). */
    private final CategoriaRepository categoriaRepository;

    /** Configuración de umbrales de stock. */
    private final StockConfig stockConfig;

    /**
     * Inyección por constructor de las dependencias del servicio.
     *
     * @param productoRepository  repositorio de productos
     * @param categoriaRepository repositorio de categorías
     * @param stockConfig         configuración de umbrales de stock
     */
    public AlertaServiceImpl(ProductoRepository productoRepository,
                             CategoriaRepository categoriaRepository,
                             StockConfig stockConfig) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.stockConfig = stockConfig;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Recorre todos los productos y filtra aquellos cuyo stock es menor
     * al umbral mínimo ({@code stockConfig.getMinimo()}). Para cada uno,
     * evalúa el nivel (BAJO o CRITICO) y construye la respuesta.
     * </p>
     */
    @Override
    public List<AlertaStockResponse> detectarStockBajo() {
        log.debug("detectarStockBajo: umbral mínimo={}, umbral crítico={}",
                stockConfig.getMinimo(), stockConfig.getCritico());

        int minimo = stockConfig.getMinimo();
        int critico = stockConfig.getCritico();

        List<AlertaStockResponse> alertas = productoRepository.findAll().stream()
                .filter(p -> p.getStock() < minimo)
                .map(p -> {
                    NivelAlerta nivel = evaluarNivelStock(p.getStock(), minimo, critico);
                    return AlertaStockResponse.of(
                            p.getId(),
                            p.getNombre(),
                            p.getStock(),
                            minimo,
                            nivel
                    );
                })
                .collect(Collectors.toList());

        log.info("Stock bajo detectado: {} producto(s) por debajo del mínimo", alertas.size());
        return alertas;
    }

    /**
     * {@inheritDoc}
     * <p>
     * La evaluación sigue una jerarquía: primero se verifica el nivel crítico,
     * luego el nivel bajo, y por defecto se asume NORMAL.
     * </p>
     */
    @Override
    public NivelAlerta evaluarNivelStock(int stockActual, int stockMinimo, int stockCritico) {
        if (stockActual <= stockCritico) {
            return NivelAlerta.CRITICO;
        }
        if (stockActual < stockMinimo) {
            return NivelAlerta.BAJO;
        }
        return NivelAlerta.NORMAL;
    }
}
