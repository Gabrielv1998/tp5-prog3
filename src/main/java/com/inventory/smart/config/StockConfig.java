package com.inventory.smart.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuración de umbrales de stock para el sistema de alertas.
 * <p>
 * Los valores se cargan desde {@code application.yml} bajo el prefijo
 * {@code inventario.stock}. Si no se especifican, se usan los valores por defecto.
 * </p>
 * <ul>
 *   <li><b>minimo</b>: umbral por debajo del cual se activa la alerta BAJO (default: 10)</li>
 *   <li><b>critico</b>: umbral por debajo del cual se activa la alerta CRITICO (default: 3)</li>
 * </ul>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Component
@Validated
@ConfigurationProperties(prefix = "inventario.stock")
public class StockConfig {

    /** Umbral mínimo de stock; por debajo se considera BAJO. */
    @Min(value = 1, message = "El stock mínimo debe ser al menos 1")
    private int minimo = 10;

    /** Umbral crítico de stock; por debajo se considera CRITICO. */
    @Min(value = 0, message = "El stock crítico no puede ser negativo")
    private int critico = 3;

    /**
     * @return el umbral mínimo de stock
     */
    public int getMinimo() {
        return minimo;
    }

    /**
     * @param minimo nuevo valor para el umbral mínimo
     */
    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    /**
     * @return el umbral crítico de stock
     */
    public int getCritico() {
        return critico;
    }

    /**
     * @param critico nuevo valor para el umbral crítico
     */
    public void setCritico(int critico) {
        this.critico = critico;
    }
}
