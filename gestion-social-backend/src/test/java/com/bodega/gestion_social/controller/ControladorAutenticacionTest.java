package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.config.TestSecurityConfig;
import com.bodega.gestion_social.dto.request.SolicitudLogin;
import com.bodega.gestion_social.dto.response.RespuestaAutenticacion;
import com.bodega.gestion_social.service.ServicioAutenticacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorAutenticacion.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ControladorAutenticacionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioAutenticacion servicioAutenticacion;

    @Nested
    @DisplayName("POST /api/auth/login - Iniciar sesión")
    class Login {

        @Test
        @DisplayName("400: Correo vacío debe retornar error de validación")
        void correoVacio_retorna400() throws Exception {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setCorreo("");
            solicitud.setContrasena("secreto123");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("correo")));
        }

        @Test
        @DisplayName("400: Correo con formato inválido debe retornar error de validación")
        void correoInvalido_retorna400() throws Exception {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setCorreo("correo-sin-arroba");
            solicitud.setContrasena("secreto123");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("correo")));
        }

        @Test
        @DisplayName("400: Contraseña vacía debe retornar error de validación")
        void contrasenaVacia_retorna400() throws Exception {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setCorreo("admin@bodega.com");
            solicitud.setContrasena("");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("contraseña")));
        }

        @Test
        @DisplayName("401: Credenciales incorrectas debe retornar UNAUTHORIZED")
        void credencialesIncorrectas_retorna401() throws Exception {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setCorreo("admin@bodega.com");
            solicitud.setContrasena("contrasenaMala");

            when(servicioAutenticacion.login(any()))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("inválidas")));
        }

        @Test
        @DisplayName("200: Login exitoso debe retornar token")
        void loginExitoso_retorna200ConToken() throws Exception {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setCorreo("admin@bodega.com");
            solicitud.setContrasena("admin123");

            RespuestaAutenticacion respuesta = new RespuestaAutenticacion(
                    "token-jwt-simulado", "Admin", "ADMIN");

            when(servicioAutenticacion.login(any())).thenReturn(respuesta);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("token-jwt-simulado"))
                    .andExpect(jsonPath("$.data.nombre").value("Admin"))
                    .andExpect(jsonPath("$.data.rol").value("ADMIN"));
        }
    }
}