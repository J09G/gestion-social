package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.request.SolicitudProducto;
import com.bodega.gestion_social.dto.response.RespuestaProducto;

import java.util.List;

public interface ServicioProducto {

    List<RespuestaProducto> listarConFiltros(String idCategoria, String estado, String nombre);

    RespuestaProducto crear(SolicitudProducto solicitud);

    RespuestaProducto actualizar(String id, SolicitudProducto solicitud);

    void desactivar(String id);

    List<RespuestaProducto> obtenerAlertas();
}