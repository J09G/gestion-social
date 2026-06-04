package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaAutenticacion {
    private String token;
    private String nombre;
    private String rol;
}