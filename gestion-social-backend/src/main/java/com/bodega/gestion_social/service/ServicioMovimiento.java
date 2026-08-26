package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.request.SolicitudMovimiento;
import com.bodega.gestion_social.dto.response.RespuestaMovimiento;
import com.bodega.gestion_social.dto.response.RespuestaPaginada;
import com.bodega.gestion_social.model.TipoMovimiento;

import java.time.LocalDateTime;

public interface ServicioMovimiento {

    RespuestaMovimiento registrar(SolicitudMovimiento solicitud, String idUsuario, String nombreUsuario);

    RespuestaPaginada<RespuestaMovimiento> listar(LocalDateTime desde, LocalDateTime hasta, TipoMovimiento tipo, String idProducto, int pagina, int tamanio);
}