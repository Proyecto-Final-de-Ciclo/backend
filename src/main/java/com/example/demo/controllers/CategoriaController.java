package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Categoria;
import com.example.demo.services.CategoriaService;
import com.example.demo.utils.SanitizerUtil;

import jakarta.validation.Valid;

@RestController
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // LISTA CATEGORÍAS
    @GetMapping("/categorias")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }


    // DEVUELVE CATEGORÍA
    @GetMapping("/categoria/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerPorId(id);
        if (categoria == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoria);
    }


    // CREA CATEGORÍA
    @PostMapping("/categoria")
    public ResponseEntity<?> create(@Valid @RequestBody Categoria categoria) {
        try {
            categoria.setNombre(SanitizerUtil.sanitize(categoria.getNombre(), "categoría sin nombre"));
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.añadir(categoria));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // EDITAR CATEGORÍA
    @PutMapping("/categoria/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        categoria.setId(id);
        try {
            categoria.setNombre(SanitizerUtil.sanitize(categoria.getNombre(), "categoría sin nombre"));
            return ResponseEntity.ok(categoriaService.editar(categoria));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // BORRAR CATEGORÍA. no se puede si tiene anuncios asociados.
    @DeleteMapping("/categoria/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoriaService.borrar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}