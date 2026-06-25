package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.request.SolicitudCategoria;
import com.bodega.gestion_social.dto.response.RespuestaCategoria;

import java.util.List;

public interface ServicioCategoria {

    List<RespuestaCategoria> listarTodas();

    RespuestaCategoria crear(SolicitudCategoria solicitud);

    RespuestaCategoria actualizar(String id, SolicitudCategoria solicitud);
}