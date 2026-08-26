package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaDashboardStats {
    private long productosActivos;
    private long categorias;
    private long movimientos;
}