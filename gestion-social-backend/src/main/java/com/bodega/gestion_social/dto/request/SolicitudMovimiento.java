package com.bodega.gestion_social.dto.request;

import com.bodega.gestion_social.model.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SolicitudMovimiento {

    @NotBlank(message = "El id del producto es obligatorio")
    private String idProducto;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @Positive(message = "La cantidad debe ser mayor a cero")
    private int cantidad;
}