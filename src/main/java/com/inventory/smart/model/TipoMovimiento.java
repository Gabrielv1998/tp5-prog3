package com.inventory.smart.model;

/**
 * Tipos de movimiento de inventario que pueden registrarse en el sistema.
 * <ul>
 *   <li><b>ENTRADA</b>: incrementa el stock cuando ingresa mercadería</li>
 *   <li><b>SALIDA</b>: decrementa el stock cuando egresa mercadería</li>
 * </ul>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public enum TipoMovimiento {

    /** Ingreso de mercadería al inventario. */
    ENTRADA,

    /** Egreso de mercadería del inventario. */
    SALIDA
}
