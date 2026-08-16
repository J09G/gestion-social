package com.bodega.gestion_social.security;

import com.bodega.gestion_social.dto.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

        private final UtilidadJwt utilidadJwt;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud,
                                    HttpServletResponse respuesta,
                                    FilterChain cadenaFiltros)
            throws ServletException, IOException {

                String token = extraerToken(solicitud);

        if (token != null) {
            if (utilidadJwt.validarToken(token)) {
                String correo = utilidadJwt.obtenerCorreoDesdeToken(token);
                String rol = utilidadJwt.obtenerRolDesdeToken(token);
                String idUsuario = utilidadJwt.obtenerIdUsuarioDesdeToken(token);

                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                var autenticacion = new UsernamePasswordAuthenticationToken(correo, null, autoridades);
                autenticacion.setDetails(idUsuario);

                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } else {
                responderTokenInvalido(respuesta);
                return;
            }
        }

        cadenaFiltros.doFilter(solicitud, respuesta);
    }

    private void responderTokenInvalido(HttpServletResponse respuesta) throws IOException {
        respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        respuesta.setContentType("application/json");
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResponse<Void> apiResponse = ApiResponse.fail("Token JWT inválido o expirado. Inicie sesión nuevamente.");
        respuesta.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private String extraerToken(HttpServletRequest solicitud) {
        String cabecera = solicitud.getHeader("Authorization");
        if (StringUtils.hasText(cabecera) && cabecera.startsWith("Bearer ")) {
            return cabecera.substring(7);
        }
        if (StringUtils.hasText(cabecera)) {
            return "INVALIDO";
        }
        return null;
    }
}