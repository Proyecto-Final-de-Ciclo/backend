package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

// el token que guarda el front para pasarlo en las peticiones
public class JwtResponseDto {
  private String accessToken;
  private String tokenType;     // "Bearer"
  private Long id;
  private String nombre;
  private String email;
  private String rol;
}
