package com.biblioteca.digital.domain.service;

import com.biblioteca.digital.domain.model.Recomendacion;
import java.util.HashMap;
import java.util.Map;

public class RecomendacionRegistry {
    private static final Map<String, Recomendacion> prototipos = new HashMap<>();

    public static void registrar(String key, Recomendacion prototype) {
        prototipos.put(key, prototype);
    }

    public static Recomendacion crear(String key) {
        Recomendacion prototype = prototipos.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototipo no encontrado: " + key);
        }
        return prototype.copy();
    }
}
