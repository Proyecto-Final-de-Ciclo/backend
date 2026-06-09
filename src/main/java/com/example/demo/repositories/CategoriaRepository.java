package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // evitar categorias duplicadas al crear
    boolean existsByNombre(String nombre);

    // evitar categorias duplicadas al editar
    Categoria findByNombre(String nombre);
}