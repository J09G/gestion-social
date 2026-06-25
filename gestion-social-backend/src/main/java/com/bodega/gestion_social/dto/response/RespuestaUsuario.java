package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaUsuario {
    private String id;
    private String nombre;
    private String correo;
    private String rol;
    private boolean activo;
}