package com.inventory.smart.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un movimiento de inventario (entrada o salida) registrado en el sistema.
 * <p>
 * Cada movimiento está asociado a un producto mediante {@code productoId} (composición)
 * y lleva una marca de tiempo automática. Esta clase es inmutable.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class MovimientoInventario {

    /** Identificador único del movimiento. */
    private final Long id;

    /** Identificador del producto asociado al movimiento. */
    private final Long productoId;

    /** Tipo de movimiento: ENTRADA o SALIDA. */
    private final TipoMovimiento tipo;

    /** Cantidad de unidades movidas (siempre positiva). */
    private final int cantidad;

    /** Fecha y hora en que se registró el movimiento. */
    private final LocalDateTime fecha;

    /** Motivo o justificación del movimiento. */
    private final String motivo;

    /**
     * Construye un nuevo movimiento de inventario con fecha actual.
     *
     * @param id         identificador único (puede ser {@code null} para nuevos)
     * @param productoId identificador del producto asociado
     * @param tipo       tipo de movimiento
     * @param cantidad   cantidad de unidades
     * @param motivo     motivo del movimiento
     * @throws NullPointerException     si {@code productoId}, {@code tipo} o {@code motivo} son {@code null}
     * @throws IllegalArgumentException si {@code cantidad} es negativa o cero
     */
    public MovimientoInventario(Long id, Long productoId, TipoMovimiento tipo, int cantidad, String motivo) {
        this(id, productoId, tipo, cantidad, LocalDateTime.now(), motivo);
    }

    /**
     * Construye un nuevo movimiento de inventario con fecha explícita.
     *
     * @param id         identificador único
     * @param productoId identificador del producto asociado
     * @param tipo       tipo de movimiento
     * @param cantidad   cantidad de unidades
     * @param fecha      fecha y hora del movimiento
     * @param motivo     motivo del movimiento
     * @throws NullPointerException     si {@code productoId}, {@code tipo}, {@code fecha} o {@code motivo} son {@code null}
     * @throws IllegalArgumentException si {@code cantidad} es negativa o cero
     */
    public MovimientoInventario(Long id, Long productoId, TipoMovimiento tipo, int cantidad,
                                LocalDateTime fecha, String motivo) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.id = id;
        this.productoId = Objects.requireNonNull(productoId, "El producto no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de movimiento no puede ser nulo");
        this.cantidad = cantidad;
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        this.motivo = Objects.requireNonNull(motivo, "El motivo no puede ser nulo");
    }

    /**
     * @return el identificador único del movimiento
     */
    public Long getId() {
        return id;
    }

    /**
     * @return el identificador del producto asociado
     */
    public Long getProductoId() {
        return productoId;
    }

    /**
     * @return el tipo de movimiento
     */
    public TipoMovimiento getTipo() {
        return tipo;
    }

    /**
     * @return la cantidad de unidades movidas
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * @return la fecha y hora del movimiento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * @return el motivo del movimiento
     */
    public String getMotivo() {
        return motivo;
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
        if (!(o instanceof MovimientoInventario that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * @return código hash basado en el {@code id}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * @return representación en cadena del movimiento
     */
    @Override
    public String toString() {
        return "MovimientoInventario{id=" + id + ", productoId=" + productoId
                + ", tipo=" + tipo + ", cantidad=" + cantidad + ", fecha=" + fecha
                + ", motivo='" + motivo + "'}";
    }
}
