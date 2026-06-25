package com.bodega.gestion_social.mapper;

import com.bodega.gestion_social.dto.response.RespuestaMovimiento;
import com.bodega.gestion_social.model.Movimiento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    RespuestaMovimiento toResponse(Movimiento movimiento);

    List<RespuestaMovimiento> toResponseList(List<Movimiento> movimientos);
}