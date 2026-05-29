package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class JwtResponseDto {
  private String accessToken;
  private String tokenType;     // "Bearer"
  private Long id;
  private String nombre;
  private String email;
  private String rol;
}
