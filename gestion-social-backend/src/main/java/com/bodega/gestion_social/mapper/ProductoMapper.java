package com.bodega.gestion_social.mapper;

import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "nombreCategoria", ignore = true)
    @Mapping(target = "estado", ignore = true)
    RespuestaProducto toResponse(Producto producto);

    List<RespuestaProducto> toResponseList(List<Producto> productos);
}