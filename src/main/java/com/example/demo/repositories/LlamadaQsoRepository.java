package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.LlamadaQso;
import com.example.demo.domain.Usuario;

public interface LlamadaQsoRepository extends JpaRepository<LlamadaQso, Long> {
    // llamadas activas de más reciente a más antigua
    List<LlamadaQso> findByExpiraEnAfterOrderByFechaPublicacionDesc(LocalDateTime ahora);
    
    List<LlamadaQso> findByUsuario(Usuario usuario);
    
    // llamadas expiradas
    List<LlamadaQso> findByExpiraEnBefore(LocalDateTime ahora);
}