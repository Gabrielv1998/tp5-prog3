package com.inventory.smart.model;

import java.util.Objects;

/**
 * Representa una categoría de productos dentro del inventario.
 * <p>
 * Clase inmutable con campos {@code final}; la igualdad se determina exclusivamente
 * por el identificador {@code id}.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class Categoria {

    /** Identificador único de la categoría. */
    private final Long id;

    /** Nombre descriptivo de la categoría. */
    private final String nombre;

    /** Descripción extendida de la categoría. */
    private final String descripcion;

    /**
     * Construye una nueva categoría con todos sus atributos.
     *
     * @param id          identificador único (puede ser {@code null} para nuevas)
     * @param nombre      nombre de la categoría
     * @param descripcion descripción de la categoría
     * @throws NullPointerException si {@code nombre} o {@code descripcion} son {@code null}
     */
    public Categoria(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
    }

    /**
     * @return el identificador único de la categoría
     */
    public Long getId() {
        return id;
    }

    /**
     * @return el nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return la descripción de la categoría
     */
    public String getDescripcion() {
        return descripcion;
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
        if (!(o instanceof Categoria that)) return false;
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
     * @return representación en cadena de la categoría
     */
    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion + "'}";
    }
}
