package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// para registrarse
public class SignupDto {

  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
  private String nombre;

  @NotBlank(message = "El email es obligatorio")
  @Size(max = 50, message = "El email no puede tener más de 50 caracteres")
  @Email(message = "El email no es válido")
  private String email;

  private String rol;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 6, max = 40, message = "La contraseña debe tener entre 6 y 40 caracteres")
  private String password;
}