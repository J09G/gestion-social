package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudMovimiento;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaMovimiento;
import com.bodega.gestion_social.dto.response.RespuestaPaginada;
import com.bodega.gestion_social.model.TipoMovimiento;
import com.bodega.gestion_social.service.ServicioMovimiento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class ControladorMovimiento {

    private final ServicioMovimiento servicioMovimiento;

    @PostMapping
    public ResponseEntity<ApiResponse<RespuestaMovimiento>> registrar(
            @Valid @RequestBody SolicitudMovimiento solicitud) {

        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        String correo = autenticacion.getName();
        String idUsuario = (String) autenticacion.getDetails();

        RespuestaMovimiento respuesta = servicioMovimiento.registrar(solicitud, idUsuario, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(respuesta, "Movimiento registrado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RespuestaPaginada<RespuestaMovimiento>>> listar(
            @RequestParam(required = false) LocalDateTime desde,
            @RequestParam(required = false) LocalDateTime hasta,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) String idProducto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        RespuestaPaginada<RespuestaMovimiento> respuesta = servicioMovimiento.listar(desde, hasta, tipo, idProducto, page, size);
        return ResponseEntity.ok(ApiResponse.ok(respuesta, "Movimientos listados exitosamente"));
    }
}