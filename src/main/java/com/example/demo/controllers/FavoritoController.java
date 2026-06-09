package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Favorito;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.AnuncioConImagenesDto;
import com.example.demo.dto.FavoritoResponseDto;
import com.example.demo.services.AnuncioService;
import com.example.demo.services.FavoritoService;
import com.example.demo.services.UsuarioService;

@RestController
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private UsuarioService usuarioService;

    // MIS FAVORITOS. pasa los favoritos a los dto de anuncios que se pueden mostrar.
    @GetMapping("/favoritos")
    public ResponseEntity<?> getMisFavoritos() {
        Usuario usuario = usuarioService.obtenerUsuarioConectado();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Favorito> favoritos = favoritoService.obtenerPorUsuario(usuario);
        List<AnuncioConImagenesDto> dtos = new ArrayList<>();

        for (Favorito favorito : favoritos) {
            dtos.add(anuncioService.toDto(favorito.getAnuncio()));
        }

        return ResponseEntity.ok(dtos);
    }


    // DETERMINA SI EL ANUNCIO ES FAVORITO DEL USUARIO CONECTADO. para el detalle, para ver 
    // si pintar la estrella o no
    @GetMapping("/favorito/{anuncioId}")
    public ResponseEntity<?> esFavorito(@PathVariable Long anuncioId) {
        Usuario usuario = usuarioService.obtenerUsuarioConectado();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Anuncio anuncio = anuncioService.obtenerPorId(anuncioId);
        if (anuncio == null) {
            return ResponseEntity.notFound().build();
        }
        boolean esFav = favoritoService.esFavorito(usuario, anuncio);
        return ResponseEntity.ok(new FavoritoResponseDto(esFav));
    }


    // AÑADIR A FAVORITOS.
    @PostMapping("/favorito/{anuncioId}")
    public ResponseEntity<?> addFavorito(@PathVariable Long anuncioId) {
        Usuario usuario = usuarioService.obtenerUsuarioConectado();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Anuncio anuncio = anuncioService.obtenerPorId(anuncioId);
        if (anuncio == null) {
            return ResponseEntity.notFound().build();
        }
        Favorito favorito = favoritoService.añadir(usuario, anuncio);
        if (favorito == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
    }


    // QUITAR DE FAVORITOS
    @DeleteMapping("/favorito/{anuncioId}")
    public ResponseEntity<?> removeFavorito(@PathVariable Long anuncioId) {
        Usuario usuario = usuarioService.obtenerUsuarioConectado();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Anuncio anuncio = anuncioService.obtenerPorId(anuncioId);
        if (anuncio == null) {
            return ResponseEntity.notFound().build();
        }
        favoritoService.borrar(usuario, anuncio);
        return ResponseEntity.noContent().build();
    }
}