package com.example.demo.utils;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizerUtil {

    // política que elimina scripts
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    //  sanitiza un texto eliminando XSS y código malicioso, si el texto era codigo malicioso
    // queda vacio y se asign un valor por defecto
    public static String sanitize(String texto, String valorPorDefecto) {
        if (texto == null) return valorPorDefecto;
        String resultado = POLICY.sanitize(texto).strip();
        return resultado.isEmpty() ? valorPorDefecto : resultado;
    }

    // sin valor por defecto (null)
    public static String sanitize(String texto) {
        return sanitize(texto, null);
    }
}