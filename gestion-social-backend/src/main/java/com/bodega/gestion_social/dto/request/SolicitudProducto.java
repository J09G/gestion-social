package com.bodega.gestion_social.dto.request;

import lombok.Data;

@Data
public class SolicitudProducto {
    private String nombre;
    private String idCategoria;
    private boolean tieneTalla;
    private String talla;
    private int stockActual;
    private int umbralCritico;
}