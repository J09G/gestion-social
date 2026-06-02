package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("usuarios")
public class Usuario {

    @Id
    private String id;
    private String nombre;
    private String correo;
    private String hashContrasena;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}