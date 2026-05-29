package com.example.demo.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

@Entity
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String nombre;

  @NotBlank
  private String password;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private LocalDate fechaRegistro;

  private Rol rol;

  private String descripcion;

  private String indicativo;

  private String localizacion;

  private boolean mostrarEmail;

  private String nombreReal;
  private String apellidos;

  private String activoDesde;
  private String modos;
  private boolean qslBuro;
  private String descripcionRadio;

  private boolean mostrarApellidos;
  private boolean mostrarNombreReal;
  private boolean mostrarUbicacion;
  private boolean mostrarDescripcionVendedor;
  private boolean mostrarDescripcionRadio;
  private boolean mostrarActivoDesde;
}