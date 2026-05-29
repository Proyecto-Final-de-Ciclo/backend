package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.NoticiaDto;
import com.example.demo.services.NoticiaService;

@RestController
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    @GetMapping("/noticias")
    public ResponseEntity<?> getNoticias() {
        try {
            List<NoticiaDto> noticias = noticiaService.obtenerNoticias();
            return ResponseEntity.ok(noticias);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener las noticias");
        }
    }
}