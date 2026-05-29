package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.LlamadaQso;
import com.example.demo.domain.Usuario;
import com.example.demo.services.LlamadaQsoService;
import com.example.demo.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
public class LlamadaQsoController {

    @Autowired
    private LlamadaQsoService llamadaQsoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/llamadas")
    public ResponseEntity<?> obtenerActivas() {
        List<LlamadaQso> llamadas = llamadaQsoService.obtenerActivas();
        if (llamadas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(llamadas);
    }

    @PostMapping("/llamada")
    public ResponseEntity<?> publicar(
            @Valid @RequestBody LlamadaQso llamada,
            @RequestParam int minutos) {

        Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();

        if (usuarioConectado.getIndicativo() == null
                || usuarioConectado.getIndicativo().isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Necesitas añadir tu indicativo en tu perfil para publicar una llamada");
        }

        if (minutos != 30 && minutos != 120 && minutos != 1440) {
            return ResponseEntity.badRequest()
                    .body("El tiempo de expiración debe ser 30, 120 o 1440 minutos");
        }

        llamada.setUsuario(usuarioConectado);
        LlamadaQso nueva = llamadaQsoService.publicar(llamada, minutos);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @DeleteMapping("/llamada/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {

        LlamadaQso llamada = llamadaQsoService.obtenerPorId(id);
        if (llamada == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
        boolean esAdmin = usuarioConectado.getRol().name().equals("ADMIN");
        boolean esDueno = llamada.getUsuario() != null
                && llamada.getUsuario().getId().equals(usuarioConectado.getId());

        if (!esAdmin && !esDueno) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        llamadaQsoService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}