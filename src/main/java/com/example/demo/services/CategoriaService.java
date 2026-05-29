package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Categoria;
import com.example.demo.repositories.AnuncioRepository;
import com.example.demo.repositories.CategoriaRepository;

@Service
@Primary
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepositorio;

    @Autowired
    private AnuncioRepository anuncioRepositorio;

    public List<Categoria> obtenerTodas() {
        return categoriaRepositorio.findAll();
    }

    public Categoria obtenerPorId(Long id) {
        return categoriaRepositorio.findById(id).orElse(null);
    }

    public Categoria añadir(Categoria categoria) {
        if (categoriaRepositorio.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }
        return categoriaRepositorio.save(categoria);
    }

    public Categoria editar(Categoria categoria) {
        if (categoriaRepositorio.findById(categoria.getId()).isEmpty()) {
            throw new RuntimeException("Categoría no encontrada con id: " + categoria.getId());
        }
        return categoriaRepositorio.save(categoria);
    }

    public void borrar(Long id) {
        if (categoriaRepositorio.findById(id).isEmpty()) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }
        if (anuncioRepositorio.existsByCategoria_Id(id)) {
            throw new RuntimeException("No se puede eliminar una categoría que tiene anuncios asociados");
        }
        categoriaRepositorio.deleteById(id);
    }
}