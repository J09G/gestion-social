package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.response.RespuestaDashboardStats;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.MovimientoRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.service.ServicioDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioDashboardImpl implements ServicioDashboard {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public RespuestaDashboardStats obtenerEstadisticas() {
        long productosActivos = productoRepository.countByActivoTrue();
        long categorias = categoriaRepository.count();
        long movimientos = movimientoRepository.count();

        return new RespuestaDashboardStats(productosActivos, categorias, movimientos);
    }
}