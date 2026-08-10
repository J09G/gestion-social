package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Movimiento;
import com.bodega.gestion_social.model.TipoMovimiento;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends MongoRepository<Movimiento, String> {

        List<Movimiento> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin, Sort sort);

    List<Movimiento> findByTipoAndFechaHoraBetween(TipoMovimiento tipo, LocalDateTime inicio, LocalDateTime fin, Sort sort);

    List<Movimiento> findByIdProducto(String idProducto, Sort sort);

    List<Movimiento> findAll(Sort sort);
}