package com.bodega.gestion_social.exception;

import com.bodega.gestion_social.dto.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ═══════════════ 404 - Recurso no encontrado ═══════════════

    @Test
    @DisplayName("404: Debe retornar NOT_FOUND con mensaje que incluye entidad, campo y valor")
    void handleRecursoNoEncontrado_retorna404ConMensajeCompleto() {
        var ex = new RecursoNoEncontradoException("Producto", "id", "abc123");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleRecursoNoEncontrado(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).contains("Producto");
        assertThat(respuesta.getBody().getMessage()).contains("abc123");
    }

    // ═══════════════ 409 - Conflicto / Duplicado ═══════════════

    @Test
    @DisplayName("409: Debe retornar CONFLICT cuando se intenta crear un recurso duplicado")
    void handleRecursoDuplicado_retorna409() {
        var ex = new RecursoDuplicadoException("Usuario", "correo", "admin@bodega.com");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleRecursoDuplicado(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).contains("Usuario");
        assertThat(respuesta.getBody().getMessage()).contains("correo");
        assertThat(respuesta.getBody().getMessage()).contains("admin@bodega.com");
    }

    // ═══════════════ 400 - Argumento inválido ═══════════════

    @Test
    @DisplayName("400: Debe retornar BAD_REQUEST cuando se lanza IllegalArgumentException")
    void handleArgumentoInvalido_retorna400() {
        var ex = new IllegalArgumentException("El stock no puede ser negativo");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleArgumentoInvalido(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).contains("stock");
    }

    // ═══════════════ 401 - Credenciales inválidas ═══════════════

    @Test
    @DisplayName("401: Debe retornar UNAUTHORIZED cuando las credenciales son inválidas")
    void handleCredenciales_retorna401() {
        var ex = new BadCredentialsException("Credenciales inválidas");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleCredenciales(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).contains("inválidas");
    }

    // ═══════════════ 422 - Estado inválido ═══════════════

    @Test
    @DisplayName("422: Debe retornar UNPROCESSABLE_ENTITY cuando la operación no es válida para el estado actual")
    void handleEstadoInvalido_retorna422() {
        var ex = new IllegalStateException("El producto ya está desactivado");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleEstadoInvalido(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).contains("desactivado");
    }

    // ═══════════════ 500 - Error interno ═══════════════

    @Test
    @DisplayName("500: Debe retornar INTERNAL_SERVER_ERROR sin exponer detalles internos")
    void handleGeneral_retorna500SinDetalles() {
        var ex = new NullPointerException("Error en ServicioProductoImpl.java:142");

        ResponseEntity<ApiResponse<Void>> respuesta = handler.handleGeneral(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().isSuccess()).isFalse();
        assertThat(respuesta.getBody().getMessage()).isEqualTo("Error interno del servidor");
        // Verificar que NO filtra información sensible
        assertThat(respuesta.getBody().getMessage()).doesNotContain("NullPointerException");
        assertThat(respuesta.getBody().getMessage()).doesNotContain("ServicioProductoImpl");
    }
}