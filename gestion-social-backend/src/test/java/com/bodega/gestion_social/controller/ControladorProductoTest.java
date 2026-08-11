package com.bodega.gestion_social.controller;

import com.bodega.gestion_social.config.TestSecurityConfig;
import com.bodega.gestion_social.dto.request.SolicitudProducto;
import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.service.ServicioProducto;
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

@WebMvcTest(ControladorProducto.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ControladorProductoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioProducto servicioProducto;

    // ═══════════════ CASOS DE ERROR ═══════════════

    @Nested
    @DisplayName("POST /api/products - Crear producto")
    class CrearProducto {

        @Test
        @DisplayName("400: Nombre vacío debe retornar error de validación")
        void nombreVacio_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setNombre("");

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("nombre")));
        }

        @Test
        @DisplayName("400: Nombre nulo debe retornar error de validación")
        void nombreNulo_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setNombre(null);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("400: Categoría vacía debe retornar error de validación")
        void categoriaVacia_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setIdCategoria("");

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("categoría")));
        }

        @Test
        @DisplayName("400: Stock negativo debe retornar error de validación")
        void stockNegativo_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setStockActual(-5);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("stock")));
        }

        @Test
        @DisplayName("400: Umbral crítico negativo debe retornar error de validación")
        void umbralCriticoNegativo_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setUmbralCritico(-3);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("umbral")));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id} - Actualizar producto")
    class ActualizarProducto {

        @Test
        @DisplayName("404: Producto no encontrado debe retornar NOT_FOUND")
        void productoNoExiste_retorna404() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            String idInexistente = "64a1b2c3d4e5f6a7b8c9d000";

            when(servicioProducto.actualizar(any(), any()))
                    .thenThrow(new RecursoNoEncontradoException("Producto", "id", idInexistente));

            mockMvc.perform(put("/api/products/{id}", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Producto")));
        }

        @Test
        @DisplayName("400: Datos inválidos al actualizar debe retornar error de validación")
        void datosInvalidos_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setNombre("");

            mockMvc.perform(put("/api/products/{id}", "idCualquiera")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("400: Stock negativo al actualizar debe retornar error de validación")
        void stockNegativoAlActualizar_retorna400() throws Exception {
            SolicitudProducto solicitud = solicitudValida();
            solicitud.setStockActual(-1);

            mockMvc.perform(put("/api/products/{id}", "idCualquiera")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitud)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("PATCH /api/products/{id}/deactivate - Desactivar producto")
    class DesactivarProducto {

        @Test
        @DisplayName("404: Intentar desactivar producto inexistente debe retornar NOT_FOUND")
        void productoNoExiste_retorna404() throws Exception {
            String idInexistente = "64a1b2c3d4e5f6a7b8c9d000";

            doThrow(new RecursoNoEncontradoException("Producto", "id", idInexistente))
                    .when(servicioProducto).desactivar(idInexistente);

            mockMvc.perform(patch("/api/products/{id}/deactivate", idInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Producto")));
        }
    }

    // ═══════════════ Método helper ═══════════════

    private SolicitudProducto solicitudValida() {
        SolicitudProducto s = new SolicitudProducto();
        s.setNombre("Arroz");
        s.setIdCategoria("cat123");
        s.setStockActual(100);
        s.setUmbralCritico(10);
        s.setTieneTalla(false);
        return s;
    }
}