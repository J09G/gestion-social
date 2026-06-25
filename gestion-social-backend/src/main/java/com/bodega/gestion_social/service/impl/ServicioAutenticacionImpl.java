package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudLogin;
import com.bodega.gestion_social.dto.response.RespuestaAutenticacion;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.model.Usuario;
import com.bodega.gestion_social.repository.UsuarioRepository;
import com.bodega.gestion_social.security.UtilidadJwt;
import com.bodega.gestion_social.service.ServicioAutenticacion;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioAutenticacionImpl implements ServicioAutenticacion {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorContrasena;
    private final UtilidadJwt utilidadJwt;

    @Override
    public RespuestaAutenticacion login(SolicitudLogin solicitud) {
        Usuario usuario = usuarioRepository.findByCorreo(solicitud.getCorreo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new RecursoNoEncontradoException("Usuario desactivado");
        }

        if (!codificadorContrasena.matches(solicitud.getContrasena(), usuario.getHashContrasena())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = utilidadJwt.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getRol().name());

        return new RespuestaAutenticacion(token, usuario.getNombre(), usuario.getRol().name());
    }
}