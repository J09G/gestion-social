package com.bodega.gestion_social.dto.request;

import lombok.Data;

@Data
public class SolicitudUsuario {
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
}