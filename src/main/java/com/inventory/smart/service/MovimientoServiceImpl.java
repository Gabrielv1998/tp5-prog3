package com.inventory.smart.service;

import com.inventory.smart.dto.MovimientoRequest;
import com.inventory.smart.dto.MovimientoResponse;
import com.inventory.smart.exception.InsufficientStockException;
import com.inventory.smart.exception.ResourceNotFoundException;
import com.inventory.smart.model.MovimientoInventario;
import com.inventory.smart.model.Producto;
import com.inventory.smart.model.TipoMovimiento;
import com.inventory.smart.repository.MovimientoRepository;
import com.inventory.smart.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de movimientos de inventario.
 * <p>
 * Coordina las operaciones entre {@link MovimientoRepository} y {@link ProductoRepository}.
 * La lógica central de actualización de stock es atómica gracias al uso de
 * {@link Producto#incrementarStock(int)} y {@link Producto#decrementarStock(int)},
 * que utilizan {@link java.util.concurrent.atomic.AtomicInteger}.
 * </p>
 *
 * @author Docente de Programación III
 * @since 1.0
 */
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoServiceImpl.class);

    /** Repositorio de movimientos. */
    private final MovimientoRepository movimientoRepository;

    /** Repositorio de productos para actualizar stock. */
    private final ProductoRepository productoRepository;

    /**
     * Inyección por constructor de las dependencias del servicio.
     *
     * @param movimientoRepository repositorio de movimientos
     * @param productoRepository   repositorio de productos
     */
    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                 ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Flujo:
     * <ol>
     *   <li>Valida que el producto exista</li>
     *   <li>Si es ENTRADA: incrementa el stock atómicamente</li>
     *   <li>Si es SALIDA: decrementa el stock atómicamente; lanza
     *       {@link InsufficientStockException} si no alcanza</li>
     *   <li>Registra el movimiento en el repositorio</li>
     * </ol>
     * </p>
     */
    @Override
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        log.debug("registrarMovimiento: productoId={}, tipo={}, cantidad={}",
                request.productoId(), request.tipo(), request.cantidad());

        // Validar que el producto existe
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.productoId()));

        // Actualizar stock según tipo de movimiento
        if (request.tipo() == TipoMovimiento.ENTRADA) {
            producto.incrementarStock(request.cantidad());
            log.info("ENTRADA: productoId={}, cantidad={}, nuevoStock={}",
                    producto.getId(), request.cantidad(), producto.getStock());
        } else {
            boolean exitoso = producto.decrementarStock(request.cantidad());
            if (!exitoso) {
                throw new InsufficientStockException(
                        producto.getId(), producto.getStock(), request.cantidad());
            }
            log.info("SALIDA: productoId={}, cantidad={}, nuevoStock={}",
                    producto.getId(), request.cantidad(), producto.getStock());
        }

        // Guardar el producto con el stock actualizado (lo re-guardamos para persistir el cambio)
        productoRepository.save(producto);

        // Registrar el movimiento
        MovimientoInventario movimiento = new MovimientoInventario(
                null,
                request.productoId(),
                request.tipo(),
                request.cantidad(),
                request.motivo()
        );

        MovimientoInventario saved = movimientoRepository.save(movimiento);
        return MovimientoResponse.fromMovimiento(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MovimientoResponse> historialPorProducto(Long productoId) {
        log.debug("historialPorProducto: productoId={}", productoId);
        return movimientoRepository.findByProductoId(productoId).stream()
                .map(MovimientoResponse::fromMovimiento)
                .collect(Collectors.toList());
    }
}
