package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaReporteMensual;
import com.bodega.gestion_social.service.ServicioReporte;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ControladorReporte {

    private final ServicioReporte servicioReporte;

    @GetMapping("/reports/monthly")
    public ResponseEntity<ApiResponse<List<RespuestaReporteMensual>>> reporteMensual(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(servicioReporte.obtenerReporteMensual(year, month), "Reporte mensual generado exitosamente"));
    }

    @GetMapping("/exports/inventory")
    public ResponseEntity<byte[]> exportarInventario() {
        byte[] excel = servicioReporte.exportarInventarioExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/exports/monthly")
    public ResponseEntity<byte[]> exportarReporteMensual(
            @RequestParam int year,
            @RequestParam int month) {
        byte[] excel = servicioReporte.exportarReporteMensualExcel(year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-mensual.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}