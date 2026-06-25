package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaProducto {
    private String id;
    private String nombre;
    private String idCategoria;
    private String nombreCategoria;
    private boolean tieneTalla;
    private String talla;
    private int stockActual;
    private int umbralCritico;
    private String estado;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}