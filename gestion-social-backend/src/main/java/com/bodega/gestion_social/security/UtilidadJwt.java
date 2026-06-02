package com.bodega.gestion_social.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class UtilidadJwt {

    private final SecretKey claveSecreta;
    private final long expiracion;

    public UtilidadJwt(
            @Value("${jwt.secreto}") String secreto,
            @Value("${jwt.expiracion}") long expiracion
    ) {
        this.claveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracion = expiracion;
    }

    public String generarToken(String idUsuario, String correo, String rol) {
        Date ahora = new Date();
        Date expiracionFecha = new Date(ahora.getTime() + expiracion);

        return Jwts.builder()
                .subject(correo)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracionFecha)
                .signWith(claveSecreta)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            analizarToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String obtenerCorreoDesdeToken(String token) {
        return analizarToken(token).getPayload().getSubject();
    }

    public String obtenerIdUsuarioDesdeToken(String token) {
        return analizarToken(token).getPayload().get("idUsuario", String.class);
    }

    public String obtenerRolDesdeToken(String token) {
        return analizarToken(token).getPayload().get("rol", String.class);
    }

    private Jws<Claims> analizarToken(String token) {
        return Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token);
    }
}