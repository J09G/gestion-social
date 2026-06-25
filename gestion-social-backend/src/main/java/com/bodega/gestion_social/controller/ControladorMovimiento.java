package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudMovimiento;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaMovimiento;
import com.bodega.gestion_social.model.TipoMovimiento;
import com.bodega.gestion_social.security.UtilidadJwt;
import com.bodega.gestion_social.service.ServicioMovimiento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class ControladorMovimiento {

    private final ServicioMovimiento servicioMovimiento;
    private final UtilidadJwt utilidadJwt;

    @PostMapping
    public ResponseEntity<ApiResponse<RespuestaMovimiento>> registrar(
            @Valid @RequestBody SolicitudMovimiento solicitud,
            @RequestHeader("Authorization") String cabeceraAuth) {

        String token = cabeceraAuth.replace("Bearer ", "");
        String idUsuario = utilidadJwt.obtenerIdUsuarioDesdeToken(token);
        String correo = utilidadJwt.obtenerCorreoDesdeToken(token);

        RespuestaMovimiento respuesta = servicioMovimiento.registrar(solicitud, idUsuario, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(respuesta, "Movimiento registrado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RespuestaMovimiento>>> listar(
            @RequestParam(required = false) LocalDateTime desde,
            @RequestParam(required = false) LocalDateTime hasta,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) String idProducto) {
        return ResponseEntity.ok(ApiResponse.ok(servicioMovimiento.listar(desde, hasta, tipo, idProducto)));
    }
}