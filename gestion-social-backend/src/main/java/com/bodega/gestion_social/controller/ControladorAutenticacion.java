package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudLogin;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaAutenticacion;
import com.bodega.gestion_social.service.ServicioAutenticacion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ControladorAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<RespuestaAutenticacion>> login(@Valid @RequestBody SolicitudLogin solicitud) {
        return ResponseEntity.ok(ApiResponse.ok(servicioAutenticacion.login(solicitud), "Inicio de sesión exitoso"));
    }
}