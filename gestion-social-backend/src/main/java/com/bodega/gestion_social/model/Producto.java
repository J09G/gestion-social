package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("productos")
public class Producto {

    @Id
    private String id;
    private String nombre;
    private String idCategoria;
    private boolean tieneTalla;
    private String talla;
    private int stockActual;
    private int umbralCritico;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}