package com.inventory.smart.exception;

/**
 * Excepción lanzada cuando se intenta realizar una salida de inventario
 * pero el stock disponible es insuficiente para cubrir la cantidad solicitada.
 * <p>
 * Se utiliza para retornar respuestas HTTP 409 (Conflict).
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
public class InsufficientStockException extends RuntimeException {

    /** Identificador del producto involucrado. */
    private final Long productoId;

    /** Stock actual del producto. */
    private final int stockActual;

    /** Cantidad solicitada para la salida. */
    private final int cantidadSolicitada;

    /**
     * Construye la excepción con los detalles del conflicto de stock.
     *
     * @param productoId         identificador del producto
     * @param stockActual        stock disponible actualmente
     * @param cantidadSolicitada cantidad que se intentó retirar
     */
    public InsufficientStockException(Long productoId, int stockActual, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para producto id=%d: disponible=%d, solicitado=%d",
                productoId, stockActual, cantidadSolicitada));
        this.productoId = productoId;
        this.stockActual = stockActual;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * @return el identificador del producto
     */
    public Long getProductoId() {
        return productoId;
    }

    /**
     * @return el stock disponible
     */
    public int getStockActual() {
        return stockActual;
    }

    /**
     * @return la cantidad solicitada
     */
    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
