package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Imagen;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByAnuncio(Anuncio anuncio);
}