package com.inventory.smart.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa un producto dentro del inventario.
 * <p>
 * Clase mayormente inmutable: todos los campos son {@code final} excepto {@code stock},
 * que se modela con {@link AtomicInteger} para garantizar operaciones thread-safe
 * en un entorno concurrente. La relación con {@link Categoria} se establece por
 * composición a través de {@code categoriaId}, no por herencia.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class Producto {

    /** Identificador único del producto. */
    private final Long id;

    /** Nombre del producto. */
    private final String nombre;

    /** Descripción detallada del producto. */
    private final String descripcion;

    /** Precio unitario del producto. */
    private final double precio;

    /** Stock actual del producto (thread-safe). */
    private final AtomicInteger stock;

    /** Identificador de la categoría a la que pertenece (composición). */
    private final Long categoriaId;

    /**
     * Construye un nuevo producto con todos sus atributos.
     *
     * @param id          identificador único (puede ser {@code null} para nuevos)
     * @param nombre      nombre del producto
     * @param descripcion descripción del producto
     * @param precio      precio unitario
     * @param stock       cantidad inicial en stock
     * @param categoriaId identificador de la categoría asociada
     * @throws NullPointerException si {@code nombre}, {@code descripcion} o {@code categoriaId} son {@code null}
     * @throws IllegalArgumentException si {@code precio} o {@code stock} son negativos
     */
    public Producto(Long id, String nombre, String descripcion, double precio, int stock, Long categoriaId) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
        this.precio = precio;
        this.stock = new AtomicInteger(stock);
        this.categoriaId = Objects.requireNonNull(categoriaId, "La categoría no puede ser nula");
    }

    /**
     * @return el identificador único del producto
     */
    public Long getId() {
        return id;
    }

    /**
     * @return el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return la descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return el precio unitario del producto
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @return la cantidad actual en stock (lectura atómica)
     */
    public int getStock() {
        return stock.get();
    }

    /**
     * @return el identificador de la categoría asociada
     */
    public Long getCategoriaId() {
        return categoriaId;
    }

    /**
     * Incrementa el stock en la cantidad indicada de forma atómica.
     *
     * @param cantidad unidades a agregar (debe ser positiva)
     * @return el nuevo valor de stock
     * @throws IllegalArgumentException si {@code cantidad} es negativa o cero
     */
    public int incrementarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser positiva");
        }
        return stock.addAndGet(cantidad);
    }

    /**
     * Decrementa el stock en la cantidad indicada de forma atómica.
     *
     * @param cantidad unidades a retirar (debe ser positiva)
     * @return {@code true} si se pudo decrementar, {@code false} si el stock es insuficiente
     * @throws IllegalArgumentException si {@code cantidad} es negativa o cero
     */
    public boolean decrementarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a decrementar debe ser positiva");
        }
        int actual;
        int nuevo;
        do {
            actual = stock.get();
            if (actual < cantidad) {
                return false;
            }
            nuevo = actual - cantidad;
        } while (!stock.compareAndSet(actual, nuevo));
        return true;
    }

    /**
     * Compara por igualdad basándose exclusivamente en el {@code id}.
     *
     * @param o el objeto a comparar
     * @return {@code true} si ambos tienen el mismo {@code id} no nulo
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto producto)) return false;
        return id != null && Objects.equals(id, producto.id);
    }

    /**
     * @return código hash basado en el {@code id}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * @return representación en cadena del producto
     */
    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', precio=" + precio
                + ", stock=" + stock.get() + ", categoriaId=" + categoriaId + "}";
    }
}
