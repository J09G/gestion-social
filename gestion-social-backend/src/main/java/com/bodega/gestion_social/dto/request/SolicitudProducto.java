package com.bodega.gestion_social.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SolicitudProducto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String idCategoria;

    private boolean tieneTalla;
    private String talla;

    @PositiveOrZero(message = "El stock actual no puede ser negativo")
    private int stockActual;

    @PositiveOrZero(message = "El umbral crítico no puede ser negativo")
    private int umbralCritico;
}