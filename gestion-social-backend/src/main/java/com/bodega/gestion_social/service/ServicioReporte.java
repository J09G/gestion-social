package com.bodega.gestion_social.service;

import com.bodega.gestion_social.dto.response.RespuestaReporteMensual;

import java.util.List;

public interface ServicioReporte {

    List<RespuestaReporteMensual> obtenerReporteMensual(int anio, int mes);

    byte[] exportarInventarioExcel();

    byte[] exportarReporteMensualExcel(int anio, int mes);
}