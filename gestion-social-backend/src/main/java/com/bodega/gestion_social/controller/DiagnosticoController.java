package com.bodega.gestion_social.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DiagnosticoController {

    @Value("${spring.data.mongodb.uri:NO_DEFINIDA}")
    private String uriMongo;

    @GetMapping("/diagnostico")
    public Map<String, Object> diagnostico() {
        String variableEntorno = System.getenv("MONGODB_URI");

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mongoUriDeSpring", enmascarar(uriMongo));
        respuesta.put("variableMONGODB_URI_presente", variableEntorno != null);
        respuesta.put("variableMONGODB_URI_empiezaSrv", variableEntorno != null && variableEntorno.startsWith("mongodb+srv://"));

        return respuesta;
    }

    private String enmascarar(String uri) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("mongodb+srv://")) {
            return "mongodb+srv://***ENMASCARADO***";
        }
        return uri;
    }
}