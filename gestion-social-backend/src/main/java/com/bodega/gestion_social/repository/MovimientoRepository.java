package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Movimiento;
import com.bodega.gestion_social.model.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface MovimientoRepository extends MongoRepository<Movimiento, String> {

    Page<Movimiento> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    Page<Movimiento> findByTipoAndFechaHoraBetween(TipoMovimiento tipo, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    Page<Movimiento> findByIdProducto(String idProducto, Pageable pageable);

    Page<Movimiento> findAll(Pageable pageable);
}