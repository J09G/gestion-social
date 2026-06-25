package com.bodega.gestion_social.mapper;

import com.bodega.gestion_social.dto.response.RespuestaUsuario;
import com.bodega.gestion_social.model.Rol;
import com.bodega.gestion_social.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "rol", expression = "java(usuario.getRol() != null ? usuario.getRol().name() : null)")
    RespuestaUsuario toResponse(Usuario usuario);

    List<RespuestaUsuario> toResponseList(List<Usuario> usuarios);
}