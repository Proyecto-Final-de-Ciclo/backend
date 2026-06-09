package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.domain.Categoria;
import com.example.demo.domain.Estado;
import com.example.demo.domain.Imagen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

// anuncio con sus imágenes y con su vendedor UsuarioPublicoDto
public class AnuncioConImagenesDto {
    private Long id;
    private String nombre;
    private Double precio;
    private Estado estado;
    private LocalDate fechaPublicacion;
    private String descripcion;
    private Categoria categoria;
    private UsuarioPublicoDto usuario;
    private List<Imagen> imagenes;
}