package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaReporteMensual {
    private String nombreCategoria;
    private int totalUnidades;
}