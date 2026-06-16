package com.inventory.smart.config;

import com.inventory.smart.model.Categoria;
import com.inventory.smart.model.Producto;
import com.inventory.smart.repository.CategoriaRepository;
import com.inventory.smart.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos de prueba (seed data).
 * <p>
 * Al arrancar la aplicación, este componente carga datos de demostración:
 * 5 categorías y 25 productos distribuidos entre ellas. Algunos productos
 * se crean con stock bajo intencionalmente para probar el sistema de alertas.
 * </p>
 * <p>
 * Implementa {@link CommandLineRunner} para ejecutarse automáticamente
 * después de que el contexto de Spring esté completamente inicializado.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Inyección por constructor de los repositorios necesarios.
     *
     * @param productoRepository  repositorio de productos
     * @param categoriaRepository repositorio de categorías
     */
    public DataInitializer(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Ejecuta la inicialización de datos al arrancar la aplicación.
     *
     * @param args argumentos de línea de comandos (no usados)
     */
    @Override
    public void run(String... args) {
        log.info("=== Iniciando carga de datos de prueba ===");

        // Crear 5 categorías
        Categoria electronica = saveCategoria("Electrónica",
                "Dispositivos electrónicos, gadgets y componentes");
        Categoria alimentos = saveCategoria("Alimentos",
                "Productos alimenticios, bebidas y comestibles");
        Categoria ropa = saveCategoria("Ropa",
                "Prendas de vestir, calzado y accesorios de moda");
        Categoria hogar = saveCategoria("Hogar",
                "Artículos para el hogar, muebles y decoración");
        Categoria deportes = saveCategoria("Deportes",
                "Equipamiento deportivo, ropa deportiva y accesorios");

        // Crear 25 productos distribuidos (5 por categoría)
        saveProducto("Smartphone Galaxy X", "Smartphone de última generación con pantalla AMOLED de 6.7 pulgadas", 999.99, 50, electronica.getId());
        saveProducto("Laptop ProBook 15", "Laptop profesional con procesador i7, 16GB RAM y SSD 512GB", 1299.99, 25, electronica.getId());
        saveProducto("Auriculares Bluetooth", "Auriculares inalámbricos con cancelación de ruido activa", 149.99, 100, electronica.getId());
        saveProducto("Tablet DigitalPad", "Tablet de 10 pulgadas ideal para lectura y entretenimiento", 349.99, 30, electronica.getId());
        saveProducto("Cargador USB-C 65W", "Cargador rápido universal compatible con múltiples dispositivos", 39.99, 200, electronica.getId());

        saveProducto("Arroz Integral 1kg", "Arroz integral orgánico de grano largo, fuente natural de fibra", 4.99, 150, alimentos.getId());
        saveProducto("Aceite de Oliva Extra Virgen", "Aceite de oliva prensado en frío, 500ml. Ideal para ensaladas", 12.99, 80, alimentos.getId());
        saveProducto("Leche Descremada 1L", "Leche fresca descremada en envase tetra pack. Rica en calcio", 2.49, 8, alimentos.getId());        // Stock bajo (< 10)
        saveProducto("Galletas Integrales", "Galletas de avena integral con chips de chocolate. Paquete 200g", 3.79, 2, alimentos.getId());     // Stock crítico (<= 3)
        saveProducto("Café Molido Premium", "Café arábica 100% molido. Bolsa de 500g. Tueste medio", 8.99, 45, alimentos.getId());

        saveProducto("Camiseta Algodón Premium", "Camiseta de algodón 100% peinado. Disponible en varios colores", 29.99, 120, ropa.getId());
        saveProducto("Jeans Slim Fit", "Jeans de mezclilla elástica con corte moderno slim fit", 59.99, 60, ropa.getId());
        saveProducto("Zapatillas Running", "Zapatillas deportivas con suela de goma y amortiguación avanzada", 89.99, 5, ropa.getId());          // Stock bajo
        saveProducto("Chaqueta Impermeable", "Chaqueta cortavientos con membrana impermeable y transpirable", 129.99, 1, ropa.getId());        // Stock crítico
        saveProducto("Bufanda de Lana", "Bufanda tejida artesanalmente con lana merino. Suave y abrigada", 24.99, 35, ropa.getId());

        saveProducto("Set de Sartenes Antiadherentes", "Juego de 3 sartenes antiadherentes con mango de silicona. Diámetros 20/24/28cm", 79.99, 40, hogar.getId());
        saveProducto("Lámpara LED Inteligente", "Lámpara de escritorio con control por WiFi. Temperatura de color ajustable", 45.99, 55, hogar.getId());
        saveProducto("Organizador de Cocina", "Organizador modular apilable para cubiertos y utensilios de cocina", 19.99, 3, hogar.getId());     // Stock crítico
        saveProducto("Juego de Toallas Premium", "Set de 4 toallas de algodón egipcio 600g/m². Suaves y absorbentes", 54.99, 25, hogar.getId());
        saveProducto("Difusor de Aromas", "Difusor ultrasónico con luces LED. Capacidad 300ml. Silencioso", 34.99, 70, hogar.getId());

        saveProducto("Bicicleta de Montaña MTB", "Bicicleta de montaña con cuadro de aluminio. 21 velocidades. Ruedas 29\"", 499.99, 12, deportes.getId());
        saveProducto("Colchoneta de Yoga", "Colchoneta antideslizante de 6mm con correa de transporte incluida", 34.99, 80, deportes.getId());
        saveProducto("Pesas Ajustables 20kg", "Set de pesas con mancuernas ajustables. Barras y discos incluidos", 149.99, 7, deportes.getId());  // Stock bajo
        saveProducto("Raqueta de Tenis Profesional", "Raqueta de grafito con tecnología antivibración. Peso ligero 280g", 179.99, 0, deportes.getId()); // Stock crítico (0)
        saveProducto("Balón de Fútbol Oficial", "Balón de fútbol profesional cosido a máquina. Tamaño 5. Certificado FIFA", 39.99, 90, deportes.getId());

        log.info("=== Datos de prueba cargados exitosamente ===");
        log.info("Categorías creadas: {}", categoriaRepository.count());
        log.info("Productos creados: {}", productoRepository.count());
    }

    /**
     * Guarda una categoría y registra la operación en el log.
     *
     * @param nombre      nombre de la categoría
     * @param descripcion descripción de la categoría
     * @return la categoría guardada con su ID asignado
     */
    private Categoria saveCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria(null, nombre, descripcion);
        Categoria saved = categoriaRepository.save(categoria);
        log.debug("Categoría creada: id={}, nombre='{}'", saved.getId(), saved.getNombre());
        return saved;
    }

    /**
     * Guarda un producto y registra la operación en el log.
     *
     * @param nombre      nombre del producto
     * @param descripcion descripción del producto
     * @param precio      precio unitario
     * @param stock       cantidad inicial en stock
     * @param categoriaId identificador de la categoría asociada
     * @return el producto guardado con su ID asignado
     */
    private Producto saveProducto(String nombre, String descripcion, double precio, int stock, Long categoriaId) {
        Producto producto = new Producto(null, nombre, descripcion, precio, stock, categoriaId);
        Producto saved = productoRepository.save(producto);
        log.debug("Producto creado: id={}, nombre='{}', stock={}", saved.getId(), saved.getNombre(), saved.getStock());
        return saved;
    }
}
