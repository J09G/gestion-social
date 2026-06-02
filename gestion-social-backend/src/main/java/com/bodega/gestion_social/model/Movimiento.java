package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("movimientos")
public class Movimiento {

    @Id
    private String id;
    private String idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private TipoMovimiento tipo;
    private int cantidad;
    private String idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaHora;
}