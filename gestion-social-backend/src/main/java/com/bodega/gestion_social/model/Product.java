package com.bodega.gestion_social.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("products")
public class Product {

    @Id
    private String id;
    private String name;
    private String categoryId;
    private boolean hasSize;
    private String size;
    private int currentStock;
    private int criticalThreshold;
    private boolean active;
    private LocalDateTime createdAt;
}
