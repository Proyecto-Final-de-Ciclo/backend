package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Imagen;
import com.example.demo.repositories.ImagenRepository;

@Service
public class ImagenService {

    @Autowired
    private ImagenRepository repositorio;

    public List<Imagen> obtenerTodos() {
        return repositorio.findAll();
    }

    public Imagen obtenerPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public List<Imagen> obtenerPorAnuncio(Anuncio anuncio) {
        return repositorio.findByAnuncio(anuncio);
    }

    public Imagen añadir(Imagen imagen) {
        return repositorio.save(imagen);
    }

    public void borrar(Long id) {
        repositorio.deleteById(id);
    }
}