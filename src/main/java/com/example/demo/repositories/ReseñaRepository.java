package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.Reseña;
import com.example.demo.domain.Usuario;

public interface ReseñaRepository extends JpaRepository<Reseña, Long> {

    // reseñas de un vendedor
    List<Reseña> findByVendedorIdOrderByFechaDesc(Long vendedorId);

    // media de puntuaciones
    @Query("SELECT AVG(r.puntuacion) FROM Reseña r WHERE r.vendedor.id = :vendedorId")
    Double calcularMediaPorVendedor(@Param("vendedorId") Long vendedorId);

    // total de reseñas
    int countByVendedorId(Long vendedorId);

    // borrar todas las reseñas escritas por un usuario
    void deleteByAutor(Usuario autor);

    // borrar todas las reseñas recibidas por un usuario
    void deleteByVendedor(Usuario vendedor);
}