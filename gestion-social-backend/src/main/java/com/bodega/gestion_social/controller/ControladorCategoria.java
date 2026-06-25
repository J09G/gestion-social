package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudCategoria;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaCategoria;
import com.bodega.gestion_social.service.ServicioCategoria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ControladorCategoria {

    private final ServicioCategoria servicioCategoria;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RespuestaCategoria>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(servicioCategoria.listarTodas()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RespuestaCategoria>> crear(@Valid @RequestBody SolicitudCategoria solicitud) {
        RespuestaCategoria respuesta = servicioCategoria.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(respuesta, "Categoría creada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RespuestaCategoria>> actualizar(@PathVariable String id,
                                                                        @Valid @RequestBody SolicitudCategoria solicitud) {
        return ResponseEntity.ok(ApiResponse.ok(servicioCategoria.actualizar(id, solicitud), "Categoría actualizada exitosamente"));
    }
}