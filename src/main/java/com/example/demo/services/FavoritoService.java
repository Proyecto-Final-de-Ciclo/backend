package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Favorito;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.FavoritoRepository;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository repositorio;

    public List<Favorito> obtenerPorUsuario(Usuario usuario) {
        return repositorio.findByUsuario(usuario);
    }

    public Favorito añadir(Usuario usuario, Anuncio anuncio) {
        if (repositorio.existsByUsuarioAndAnuncio(usuario, anuncio)) {
            return null;
        }
        return repositorio.save(new Favorito(null, usuario, anuncio));
    }

    public boolean borrar(Usuario usuario, Anuncio anuncio) {
        Favorito favorito = repositorio.findByUsuarioAndAnuncio(usuario, anuncio).orElse(null);
        if (favorito == null) {
            return false;
        }
        repositorio.delete(favorito);
        return true;
    }

    public boolean esFavorito(Usuario usuario, Anuncio anuncio) {
        return repositorio.existsByUsuarioAndAnuncio(usuario, anuncio);
    }
}