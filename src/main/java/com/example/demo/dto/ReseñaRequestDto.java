package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReseñaRequestDto {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer puntuacion;
    
    @Size(max = 500)
    private String comentario;
}
