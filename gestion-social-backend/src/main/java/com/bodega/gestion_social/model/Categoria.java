package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("categorias")
public class Categoria {

    @Id
    private String id;
    private String nombre;
}