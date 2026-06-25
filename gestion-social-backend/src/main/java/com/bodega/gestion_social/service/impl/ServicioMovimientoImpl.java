package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudMovimiento;
import com.bodega.gestion_social.dto.response.RespuestaMovimiento;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.mapper.MovimientoMapper;
import com.bodega.gestion_social.model.Categoria;
import com.bodega.gestion_social.model.Movimiento;
import com.bodega.gestion_social.model.Producto;
import com.bodega.gestion_social.model.TipoMovimiento;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.MovimientoRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.service.ServicioMovimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioMovimientoImpl implements ServicioMovimiento {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    public RespuestaMovimiento registrar(SolicitudMovimiento solicitud, String idUsuario, String nombreUsuario) {
        Producto producto = productoRepository.findById(solicitud.getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", solicitud.getIdProducto()));

        if (!producto.isActivo()) {
            throw new IllegalStateException("El producto está desactivado");
        }

        if (solicitud.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStockActual() < solicitud.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + producto.getStockActual()
                        + ", cantidad solicitada: " + solicitud.getCantidad());
            }
            producto.setStockActual(producto.getStockActual() - solicitud.getCantidad());
        } else {
            producto.setStockActual(producto.getStockActual() + solicitud.getCantidad());
        }

        productoRepository.save(producto);

        String nombreCategoria = categoriaRepository.findById(producto.getIdCategoria())
                .map(Categoria::getNombre)
                .orElse("Sin categoría");

        Movimiento movimiento = new Movimiento();
        movimiento.setIdProducto(producto.getId());
        movimiento.setNombreProducto(producto.getNombre());
        movimiento.setNombreCategoria(nombreCategoria);
        movimiento.setTipo(solicitud.getTipo());
        movimiento.setCantidad(solicitud.getCantidad());
        movimiento.setIdUsuario(idUsuario);
        movimiento.setNombreUsuario(nombreUsuario);
        movimiento.setFechaHora(LocalDateTime.now());

        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Override
    public List<RespuestaMovimiento> listar(LocalDateTime desde, LocalDateTime hasta, TipoMovimiento tipo, String idProducto) {
        List<Movimiento> movimientos;

        if (idProducto != null && !idProducto.isBlank()) {
            movimientos = movimientoRepository.findByIdProducto(idProducto);
        } else if (tipo != null && desde != null && hasta != null) {
            movimientos = movimientoRepository.findByTipoAndFechaHoraBetween(tipo, desde, hasta);
        } else if (desde != null && hasta != null) {
            movimientos = movimientoRepository.findByFechaHoraBetween(desde, hasta);
        } else {
            movimientos = movimientoRepository.findAll();
        }

        return movimientoMapper.toResponseList(movimientos);
    }
}