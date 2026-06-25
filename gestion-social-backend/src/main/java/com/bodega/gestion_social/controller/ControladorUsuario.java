package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudUsuario;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaUsuario;
import com.bodega.gestion_social.service.ServicioUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ControladorUsuario {

    private final ServicioUsuario servicioUsuario;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RespuestaUsuario>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(servicioUsuario.listarTodos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RespuestaUsuario>> crear(@Valid @RequestBody SolicitudUsuario solicitud) {
        RespuestaUsuario respuesta = servicioUsuario.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(respuesta, "Usuario creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RespuestaUsuario>> actualizar(@PathVariable String id,
                                                                      @Valid @RequestBody SolicitudUsuario solicitud) {
        return ResponseEntity.ok(ApiResponse.ok(servicioUsuario.actualizar(id, solicitud), "Usuario actualizado exitosamente"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable String id) {
        servicioUsuario.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Usuario desactivado exitosamente"));
    }
}