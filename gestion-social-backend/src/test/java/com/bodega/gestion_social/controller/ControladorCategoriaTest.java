package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.config.TestSecurityConfig;
import com.bodega.gestion_social.dto.request.SolicitudCategoria;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.service.ServicioCategoria;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorCategoria.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ControladorCategoriaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioCategoria servicioCategoria;

    // ═══════════════ POST /api/categories - Crear categoría ═══════════════

    @Nested
    @DisplayName("POST /api/categories - Crear categoría")
    class CrearCategoria {

        @Test
        @DisplayName("400: Nombre vacío debe retornar error de validación")
        void nombreVacio_retorna400() throws Exception {
            SolicitudCategoria solicitud = new SolicitudCategoria();
            solicitud.setNombre("");

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("categoría")));
        }

        @Test
        @DisplayName("400: Nombre nulo debe retornar error de validación")
        void nombreNulo_retorna400() throws Exception {
            SolicitudCategoria solicitud = new SolicitudCategoria();
            solicitud.setNombre(null);

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false));
        }
    }

    // ═══════════════ PUT /api/categories/{id} - Actualizar categoría ═══════════════

    @Nested
    @DisplayName("PUT /api/categories/{id} - Actualizar categoría")
    class ActualizarCategoria {

        @Test
        @DisplayName("404: Categoría no encontrada debe retornar NOT_FOUND")
        void categoriaNoExiste_retorna404() throws Exception {
            SolicitudCategoria solicitud = new SolicitudCategoria();
            solicitud.setNombre("Bebidas");
            String idInexistente = "64a1b2c3d4e5f6a7b8c9d000";

            when(servicioCategoria.actualizar(any(), any()))
                    .thenThrow(new RecursoNoEncontradoException("Categoría", "id", idInexistente));

            mockMvc.perform(put("/api/categories/{id}", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.ok").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Categoría")));
        }

        @Test
        @DisplayName("400: Nombre vacío al actualizar debe retornar error de validación")
        void nombreVacio_retorna400() throws Exception {
            SolicitudCategoria solicitud = new SolicitudCategoria();
            solicitud.setNombre("");

            mockMvc.perform(put("/api/categories/{id}", "idCualquiera")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false));
        }
    }
}