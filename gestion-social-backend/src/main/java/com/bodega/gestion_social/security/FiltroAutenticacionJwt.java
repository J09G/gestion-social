package com.bodega.gestion_social.security;

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

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private final UtilidadJwt utilidadJwt;

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud,
                                    HttpServletResponse respuesta,
                                    FilterChain cadenaFiltros)
            throws ServletException, IOException {

        String token = extraerToken(solicitud);

        if (token != null && utilidadJwt.validarToken(token)) {
            String correo = utilidadJwt.obtenerCorreoDesdeToken(token);
            String rol = utilidadJwt.obtenerRolDesdeToken(token);

            var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
            var autenticacion = new UsernamePasswordAuthenticationToken(correo, null, autoridades);

            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        }

        cadenaFiltros.doFilter(solicitud, respuesta);
    }

    private String extraerToken(HttpServletRequest solicitud) {
        String cabecera = solicitud.getHeader("Authorization");
        if (StringUtils.hasText(cabecera) && cabecera.startsWith("Bearer ")) {
            return cabecera.substring(7);
        }
        return null;
    }
}