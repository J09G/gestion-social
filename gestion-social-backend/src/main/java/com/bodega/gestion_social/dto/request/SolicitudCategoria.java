package com.bodega.gestion_social.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitudCategoria {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;
}