package com.bodega.gestion_social.config;

import com.bodega.gestion_social.security.FiltroAutenticacionJwt;
import com.bodega.gestion_social.security.UtilidadJwt;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Configuración compartida para tests @WebMvcTest.
 * Provee mocks de los beans de seguridad para que no fallen al cargar el contexto.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public UtilidadJwt utilidadJwt() {
        return mock(UtilidadJwt.class);
    }

    @Bean
    public FiltroAutenticacionJwt filtroAutenticacionJwt() {
        return mock(FiltroAutenticacionJwt.class);
    }
}