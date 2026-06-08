package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Categoria;
import com.example.demo.domain.Estado;
import com.example.demo.domain.Usuario;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
        List<Anuncio> findByNombreContainingIgnoreCase(String cadena);

        List<Anuncio> findByCategoria(Categoria categoria);

        List<Anuncio> findByUsuario(Usuario usuario);

        boolean existsByCategoria_Id(Long categoriaId);

        @Query("SELECT a FROM Anuncio a WHERE " +
                        "(:nombre    IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
                        "(:categoriaId IS NULL OR a.categoria.id = :categoriaId) AND " +
                        "(:estado    IS NULL OR a.estado    = :estado)    AND " +
                        "(:precioMin IS NULL OR a.precio   >= :precioMin) AND " +
                        "(:precioMax IS NULL OR a.precio   <= :precioMax)")
        Page<Anuncio> findConFiltros(
                        @Param("nombre") String nombre,
                        @Param("categoriaId") Long categoriaId,
                        @Param("estado") Estado estado,
                        @Param("precioMin") Double precioMin,
                        @Param("precioMax") Double precioMax,
                        Pageable pageable);
}