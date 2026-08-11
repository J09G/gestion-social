package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.config.TestSecurityConfig;
import com.bodega.gestion_social.dto.request.SolicitudUsuario;
import com.bodega.gestion_social.dto.response.RespuestaUsuario;
import com.bodega.gestion_social.exception.RecursoDuplicadoException;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.service.ServicioUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorUsuario.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ControladorUsuarioTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioUsuario servicioUsuario;

    // ═══════════════ POST /api/users - Crear usuario ═══════════════

    @Nested
    @DisplayName("POST /api/users - Crear usuario")
    class CrearUsuario {

        @Test
        @DisplayName("400: Nombre vacío debe retornar error de validación")
        void nombreVacio_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setNombre("");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("nombre")));
        }

        @Test
        @DisplayName("400: Correo con formato inválido debe retornar error de validación")
        void correoInvalido_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setCorreo("esto-no-es-un-correo");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("correo")));
        }

        @Test
        @DisplayName("400: Correo vacío debe retornar error de validación")
        void correoVacio_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setCorreo("");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("400: Contraseña vacía debe retornar error de validación")
        void contrasenaVacia_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setContrasena("");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("contraseña")));
        }

        @Test
        @DisplayName("400: Rol vacío debe retornar error de validación")
        void rolVacio_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setRol("");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("rol")));
        }

        @Test
        @DisplayName("409: Correo duplicado debe retornar CONFLICT")
        void correoDuplicado_retorna409() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();

            when(servicioUsuario.crear(any()))
                    .thenThrow(new RecursoDuplicadoException("Usuario", "correo", solicitud.getCorreo()));

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Usuario")));
        }
    }

    // ═══════════════ PUT /api/users/{id} - Actualizar usuario ═══════════════

    @Nested
    @DisplayName("PUT /api/users/{id} - Actualizar usuario")
    class ActualizarUsuario {

        @Test
        @DisplayName("404: Usuario no encontrado debe retornar NOT_FOUND")
        void usuarioNoExiste_retorna404() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            String idInexistente = "64a1b2c3d4e5f6a7b8c9d000";

            when(servicioUsuario.actualizar(any(), any()))
                    .thenThrow(new RecursoNoEncontradoException("Usuario", "id", idInexistente));

            mockMvc.perform(put("/api/users/{id}", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Usuario")));
        }

        @Test
        @DisplayName("409: Cambiar a un correo ya existente debe retornar CONFLICT")
        void correoYaExisteEnOtroUsuario_retorna409() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setCorreo("otro@bodega.com");

            when(servicioUsuario.actualizar(any(), any()))
                    .thenThrow(new RecursoDuplicadoException("Usuario", "correo", "otro@bodega.com"));

            mockMvc.perform(put("/api/users/{id}", "idExistente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Usuario")));
        }

        @Test
        @DisplayName("400: Correo inválido al actualizar debe retornar error de validación")
        void correoInvalido_retorna400() throws Exception {
            SolicitudUsuario solicitud = solicitudValida();
            solicitud.setCorreo("correo-mal-formado");

            mockMvc.perform(put("/api/users/{id}", "idExistente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ═══════════════ PATCH /api/users/{id}/deactivate - Desactivar usuario ═══════════════

    @Nested
    @DisplayName("PATCH /api/users/{id}/deactivate - Desactivar usuario")
    class DesactivarUsuario {

        @Test
        @DisplayName("404: Intentar desactivar usuario inexistente debe retornar NOT_FOUND")
        void usuarioNoExiste_retorna404() throws Exception {
            String idInexistente = "64a1b2c3d4e5f6a7b8c9d000";

            doThrow(new RecursoNoEncontradoException("Usuario", "id", idInexistente))
                    .when(servicioUsuario).desactivar(idInexistente);

            mockMvc.perform(patch("/api/users/{id}/deactivate", idInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Usuario")));
        }
    }

    // ═══════════════ Método helper ═══════════════

    private SolicitudUsuario solicitudValida() {
        SolicitudUsuario s = new SolicitudUsuario();
        s.setNombre("Juan Pérez");
        s.setCorreo("juan@bodega.com");
        s.setContrasena("secreto123");
        s.setRol("BODEGUERO");
        return s;
    }
}