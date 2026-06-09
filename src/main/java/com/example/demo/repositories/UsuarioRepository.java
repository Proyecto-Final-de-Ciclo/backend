package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Usuario findByNombre(String nombre);

  Boolean existsByNombre(String nombre);

  Boolean existsByEmail(String email);

  // para la lista de usuarios del admin y la lista de radioaficionados
  @Query("SELECT u FROM Usuario u WHERE " +
      "(:busqueda IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
      "LOWER(u.indicativo) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
  List<Usuario> buscarPorNombreEmailOIndicativo(@Param("busqueda") String busqueda);
}
