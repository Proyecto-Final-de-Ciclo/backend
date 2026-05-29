package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.LlamadaQso;
import com.example.demo.domain.Usuario;

public interface LlamadaQsoRepository extends JpaRepository<LlamadaQso, Long> {
    List<LlamadaQso> findByExpiraEnAfterOrderByFechaPublicacionDesc(LocalDateTime ahora);
    List<LlamadaQso> findByUsuario(Usuario usuario);
    List<LlamadaQso> findByExpiraEnBefore(LocalDateTime ahora);
}