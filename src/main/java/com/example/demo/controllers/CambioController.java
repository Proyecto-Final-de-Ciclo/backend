package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.CambioData;
import com.example.demo.services.ServiceCambioMoneda;

@RestController
public class CambioController {

    @Autowired
    private ServiceCambioMoneda serviceCambioMoneda;

    // CONVERSIÓN DE MONEDA
    @GetMapping("/cambio")
    public ResponseEntity<?> getCambio(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            CambioData data = serviceCambioMoneda.cambioMoneda(from, to);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Moneda no válida o error en la API externa");
        }
    }
}