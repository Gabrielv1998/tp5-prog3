package com.inventory.smart.service;

import com.inventory.smart.dto.AlertaStockResponse;
import com.inventory.smart.model.NivelAlerta;

import java.util.List;

/**
 * Servicio de negocio para la detección y evaluación de alertas de stock.
 * <p>
 * Monitorea los niveles de stock de los productos y emite alertas cuando
 * el inventario cae por debajo de los umbrales configurados.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public interface AlertaService {

    /**
     * Detecta todos los productos cuyo stock está por debajo del umbral mínimo
     * configurado en {@link com.inventory.smart.config.StockConfig}.
     *
     * @return lista de alertas con el nivel evaluado (BAJO o CRITICO)
     */
    List<AlertaStockResponse> detectarStockBajo();

    /**
     * Evalúa el nivel de alerta para un producto basado en su stock actual
     * y los umbrales configurados.
     * <ul>
     *   <li>stockActual &le; stockCritico → {@link NivelAlerta#CRITICO}</li>
     *   <li>stockActual &lt; stockMinimo → {@link NivelAlerta#BAJO}</li>
     *   <li>en caso contrario → {@link NivelAlerta#NORMAL}</li>
     * </ul>
     *
     * @param stockActual  cantidad actual en stock
     * @param stockMinimo  umbral mínimo
     * @param stockCritico umbral crítico
     * @return el nivel de alerta correspondiente
     */
    NivelAlerta evaluarNivelStock(int stockActual, int stockMinimo, int stockCritico);
}
