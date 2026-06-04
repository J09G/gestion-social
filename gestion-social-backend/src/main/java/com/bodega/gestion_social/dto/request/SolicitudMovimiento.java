package com.bodega.gestion_social.dto.request;

import com.bodega.gestion_social.model.TipoMovimiento;
import lombok.Data;

@Data
public class SolicitudMovimiento {
    private String idProducto;
    private TipoMovimiento tipo;
    private int cantidad;
}