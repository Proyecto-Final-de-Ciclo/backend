package com.example.demo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticiaDto {
    private String titulo;
    private String descripcion;
    private String enlace;
    private LocalDate fecha;
    private String fuente;
}