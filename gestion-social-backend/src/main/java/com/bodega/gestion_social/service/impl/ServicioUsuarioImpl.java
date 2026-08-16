package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudUsuario;
import com.bodega.gestion_social.dto.response.RespuestaUsuario;
import com.bodega.gestion_social.exception.RecursoDuplicadoException;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.mapper.UsuarioMapper;
import com.bodega.gestion_social.model.Rol;
import com.bodega.gestion_social.model.Usuario;
import com.bodega.gestion_social.repository.UsuarioRepository;
import com.bodega.gestion_social.service.ServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioUsuarioImpl implements ServicioUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorContrasena;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<RespuestaUsuario> listarTodos() {
        return usuarioMapper.toResponseList(usuarioRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion")));
    }

    @Override
    public RespuestaUsuario crear(SolicitudUsuario solicitud) {
        if (usuarioRepository.existsByCorreo(solicitud.getCorreo())) {
            throw new RecursoDuplicadoException("Usuario", "correo", solicitud.getCorreo());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(solicitud.getNombre());
        usuario.setCorreo(solicitud.getCorreo());
        usuario.setHashContrasena(codificadorContrasena.encode(solicitud.getContrasena()));
        usuario.setRol(validarRol(solicitud.getRol()));
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public RespuestaUsuario actualizar(String id, SolicitudUsuario solicitud) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

        // Verificar si el nuevo correo ya existe en otro usuario
        if (!usuario.getCorreo().equalsIgnoreCase(solicitud.getCorreo())
                && usuarioRepository.existsByCorreo(solicitud.getCorreo())) {
            throw new RecursoDuplicadoException("Usuario", "correo", solicitud.getCorreo());
        }

        usuario.setNombre(solicitud.getNombre());
        usuario.setCorreo(solicitud.getCorreo());
        usuario.setRol(validarRol(solicitud.getRol()));

        if (solicitud.getContrasena() != null && !solicitud.getContrasena().isBlank()) {
            usuario.setHashContrasena(codificadorContrasena.encode(solicitud.getContrasena()));
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    private Rol validarRol(String valor) {
        try {
            return Rol.valueOf(valor);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Rol inválido: '" + valor + "'. Los roles permitidos son: ADMIN, BODEGUERO");
        }
    }

    @Override
    public void desactivar(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}