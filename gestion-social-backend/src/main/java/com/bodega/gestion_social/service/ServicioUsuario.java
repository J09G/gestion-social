package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.request.SolicitudUsuario;
import com.bodega.gestion_social.dto.response.RespuestaUsuario;

import java.util.List;

public interface ServicioUsuario {

    List<RespuestaUsuario> listarTodos();

    RespuestaUsuario crear(SolicitudUsuario solicitud);

    RespuestaUsuario actualizar(String id, SolicitudUsuario solicitud);

    void desactivar(String id);
}