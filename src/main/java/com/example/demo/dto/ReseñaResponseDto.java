package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

// Reseña con su autor como UsuarioPublicoDto
public class ReseñaResponseDto {
    private Long id;
    private Integer puntuacion;
    private String comentario;
    private LocalDate fecha;
    private UsuarioPublicoDto autor;
}
