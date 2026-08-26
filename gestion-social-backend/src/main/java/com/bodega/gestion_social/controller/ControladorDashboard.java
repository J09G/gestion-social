package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaDashboardStats;
import com.bodega.gestion_social.service.ServicioDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ControladorDashboard {

    private final ServicioDashboard servicioDashboard;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<RespuestaDashboardStats>> obtenerEstadisticas() {
        return ResponseEntity.ok(ApiResponse.ok(servicioDashboard.obtenerEstadisticas(), "Estadísticas del dashboard obtenidas exitosamente"));
    }
}