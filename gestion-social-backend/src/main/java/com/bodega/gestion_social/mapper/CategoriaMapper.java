package com.bodega.gestion_social.mapper;

import com.bodega.gestion_social.dto.response.RespuestaCategoria;
import com.bodega.gestion_social.model.Categoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    RespuestaCategoria toResponse(Categoria categoria);

    List<RespuestaCategoria> toResponseList(List<Categoria> categorias);
}