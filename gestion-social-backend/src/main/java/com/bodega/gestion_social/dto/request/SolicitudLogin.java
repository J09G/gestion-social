package com.bodega.gestion_social.dto.request;

import lombok.Data;

@Data
public class SolicitudLogin {
    private String correo;
    private String contrasena;
}