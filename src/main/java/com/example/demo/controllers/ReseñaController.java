package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MessageResponse;
import com.example.demo.dto.ReseñaRequestDto;
import com.example.demo.dto.ReseñaResponseDto;
import com.example.demo.services.ReseñaService;
import com.example.demo.utils.SanitizerUtil;

import jakarta.validation.Valid;

@RestController
public class ReseñaController {

    @Autowired
    private ReseñaService reseñaService;

    // OBTENER RESEÑAS DE UN VENDEDOR
    @GetMapping("/usuario/{id}/reseñas")
    public ResponseEntity<?> getReseñas(@PathVariable Long id) {
        List<ReseñaResponseDto> reseñas = reseñaService.obtenerPorVendedor(id);
        return ResponseEntity.ok(reseñas);
    }


    // CREAR RESEÑA. 
    @PostMapping("/usuario/{id}/reseña")
    public ResponseEntity<?> crearReseña(@PathVariable Long id,
            @Valid @RequestBody ReseñaRequestDto dto) {
        try {
            dto.setComentario(SanitizerUtil.sanitize(dto.getComentario(), ""));
            ReseñaResponseDto creada = reseñaService.crear(id, dto);
            return ResponseEntity.ok(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }


    // BORRAR RESEÑA. 
    @DeleteMapping("/reseña/{id}")
    public ResponseEntity<?> borrarReseña(@PathVariable Long id) {
        try {
            reseñaService.borrar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new MessageResponse(e.getMessage()));
        }
    }
}
