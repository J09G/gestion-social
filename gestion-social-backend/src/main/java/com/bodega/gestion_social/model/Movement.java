package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("movements")
public class Movement {

    @Id
    private String id;
    private String productId;
    private String productName;     // snapshot del nombre al momento del movimiento
    private String categoryName;    // snapshot de la categoria al momento del movimiento
    private MovementType type;
    private int quantity;
    private String userId;
    private String userName;        // snapshot del nombre del usuario al momento del movimiento
    private LocalDateTime timestamp;
}
