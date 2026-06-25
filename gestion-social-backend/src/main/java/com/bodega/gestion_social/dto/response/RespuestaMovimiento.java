package com.bodega.gestion_social.dto.response;

import com.bodega.gestion_social.model.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaMovimiento {
    private String id;
    private String idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private TipoMovimiento tipo;
    private int cantidad;
    private String nombreUsuario;
    private LocalDateTime fechaHora;
}