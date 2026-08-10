package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.dto.request.SolicitudProducto;
import com.bodega.gestion_social.dto.response.ApiResponse;
import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.service.ServicioProducto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ControladorProducto {

    private final ServicioProducto servicioProducto;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RespuestaProducto>>> listar(
            @RequestParam(required = false) String idCategoria,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.ok(servicioProducto.listarConFiltros(idCategoria, estado, nombre)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RespuestaProducto>> crear(@Valid @RequestBody SolicitudProducto solicitud) {
        RespuestaProducto respuesta = servicioProducto.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(respuesta, "Producto creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RespuestaProducto>> actualizar(@PathVariable String id,
                                                                        @Valid @RequestBody SolicitudProducto solicitud) {
        return ResponseEntity.ok(ApiResponse.ok(servicioProducto.actualizar(id, solicitud), "Producto actualizado exitosamente"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable String id) {
        servicioProducto.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Producto desactivado exitosamente"));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importarExcel(@RequestParam("file") MultipartFile archivo) {
        int cantidad = servicioProducto.importarDesdeExcel(archivo);
        return ResponseEntity.ok(ApiResponse.ok(null, "Se importaron/actualizaron " + cantidad + " productos exitosamente."));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<RespuestaProducto>>> obtenerAlertas() {
        return ResponseEntity.ok(ApiResponse.ok(servicioProducto.obtenerAlertas()));
    }
}