package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudProducto;
import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.mapper.ProductoMapper;
import com.bodega.gestion_social.model.Categoria;
import com.bodega.gestion_social.model.Producto;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.service.ServicioProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioProductoImpl implements ServicioProducto {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<RespuestaProducto> listarConFiltros(String idCategoria, String estado, String nombre) {
        List<Producto> productos;

        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        } else if (idCategoria != null && !idCategoria.isBlank()) {
            productos = productoRepository.findByIdCategoriaAndActivoTrue(idCategoria);
        } else {
            productos = productoRepository.findByActivoTrue();
        }

        if ("CRITICO".equalsIgnoreCase(estado)) {
            productos = productos.stream()
                    .filter(p -> p.getStockActual() < p.getUmbralCritico())
                    .toList();
        } else if ("OK".equalsIgnoreCase(estado)) {
            productos = productos.stream()
                    .filter(p -> p.getStockActual() >= p.getUmbralCritico())
                    .toList();
        }

        return productos.stream()
                .map(this::mapearARespuesta)
                .toList();
    }

    @Override
    public RespuestaProducto crear(SolicitudProducto solicitud) {
        Producto producto = new Producto();
        producto.setNombre(solicitud.getNombre());
        producto.setIdCategoria(solicitud.getIdCategoria());
        producto.setTieneTalla(solicitud.isTieneTalla());
        producto.setTalla(solicitud.getTalla());
        producto.setStockActual(solicitud.getStockActual());
        producto.setUmbralCritico(solicitud.getUmbralCritico());
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());

        return mapearARespuesta(productoRepository.save(producto));
    }

    @Override
    public RespuestaProducto actualizar(String id, SolicitudProducto solicitud) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));

        producto.setNombre(solicitud.getNombre());
        producto.setIdCategoria(solicitud.getIdCategoria());
        producto.setTieneTalla(solicitud.isTieneTalla());
        producto.setTalla(solicitud.getTalla());
        producto.setStockActual(solicitud.getStockActual());
        producto.setUmbralCritico(solicitud.getUmbralCritico());

        return mapearARespuesta(productoRepository.save(producto));
    }

    @Override
    public void desactivar(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public List<RespuestaProducto> obtenerAlertas() {
        return productoRepository.findProductosCriticos()
                .stream()
                .map(this::mapearARespuesta)
                .toList();
    }

    private RespuestaProducto mapearARespuesta(Producto producto) {
        String nombreCategoria = categoriaRepository.findById(producto.getIdCategoria())
                .map(Categoria::getNombre)
                .orElse("Sin categoría");

        String estado = producto.getStockActual() < producto.getUmbralCritico() ? "CRITICO" : "OK";

        RespuestaProducto respuesta = productoMapper.toResponse(producto);
        respuesta.setNombreCategoria(nombreCategoria);
        respuesta.setEstado(estado);
        return respuesta;
    }
}