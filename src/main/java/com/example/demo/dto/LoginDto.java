package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// para iniciar sesión
public class LoginDto {
	@NotBlank
	private String nombre;

	@NotBlank
	private String password;
}
