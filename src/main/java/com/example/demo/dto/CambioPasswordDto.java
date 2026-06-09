package com.example.demo.dto;

import lombok.Data;

@Data

// para cambiar contraseña
public class CambioPasswordDto {
    private String passwordActual;
    private String passwordNueva;
}