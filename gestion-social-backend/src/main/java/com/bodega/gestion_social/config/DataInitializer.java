package com.bodega.gestion_social.config;

import com.bodega.gestion_social.model.Rol;
import com.bodega.gestion_social.model.Usuario;
import com.bodega.gestion_social.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorContrasena;

    @Value("${admin.correo}")
    private String correoAdmin;

    @Value("${admin.contrasena}")
    private String contrasenaAdmin;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByCorreo(correoAdmin)) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo(correoAdmin);
            admin.setHashContrasena(codificadorContrasena.encode(contrasenaAdmin));
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado: " + correoAdmin);
        } else {
            System.out.println("El usuario administrador ya existe.");
        }
    }
}