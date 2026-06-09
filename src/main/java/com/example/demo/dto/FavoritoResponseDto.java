package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

// para saber si pintar la estrella rellena
public class FavoritoResponseDto {
    private boolean esFavorito;
}