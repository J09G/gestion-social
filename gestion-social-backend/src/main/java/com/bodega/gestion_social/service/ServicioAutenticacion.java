package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.request.SolicitudLogin;
import com.bodega.gestion_social.dto.response.RespuestaAutenticacion;

public interface ServicioAutenticacion {

    RespuestaAutenticacion login(SolicitudLogin solicitud);
}