package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

// respuesta genérica, para cuando se necesita devolver solo un texto.
public class MessageResponse {
  private String message;
}
