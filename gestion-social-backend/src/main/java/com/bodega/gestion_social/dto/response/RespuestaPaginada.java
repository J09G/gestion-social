package com.bodega.gestion_social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaPaginada<T> {
    private List<T> items;
    private long total;
    private int pagina;
    private int totalPaginas;
}