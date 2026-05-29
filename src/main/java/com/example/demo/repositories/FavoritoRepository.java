package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Favorito;
import com.example.demo.domain.Usuario;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // favoritos de un usuario
    List<Favorito> findByUsuario(Usuario usuario);

    // buscar uno especifico
    Optional<Favorito> findByUsuarioAndAnuncio(Usuario usuario, Anuncio anuncio);

    // comprobar si ya existe
    boolean existsByUsuarioAndAnuncio(Usuario usuario, Anuncio anuncio);
    
    // borrar favoritos de un anuncio
    void deleteByAnuncio(Anuncio anuncio);
}