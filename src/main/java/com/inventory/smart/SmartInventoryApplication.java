package com.inventory.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Clase principal de la aplicación de Gestión de Inventario Inteligente.
 * <p>
 * Inicia el contexto de Spring Boot, habilita el escaneo automático de componentes
 * y la configuración basada en propiedades tipadas.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties
public class SmartInventoryApplication {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartInventoryApplication.class, args);
    }
}
