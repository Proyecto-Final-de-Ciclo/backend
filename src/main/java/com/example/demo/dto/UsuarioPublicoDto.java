package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

// lo público de un usuario
public class UsuarioPublicoDto {

    private Long id;
    private String nombre;
    private String rol;
    private LocalDate fechaRegistro;
    private Double mediaEstrellas;
    private Integer totalReseñas;
    private String descripcion;
    private String indicativo;
    private String localizacion;
    private String email;

    private String nombreReal;
    private String apellidos;

    private String activoDesde;
    private String modos;
    private boolean qslBuro;
    private String descripcionRadio;

    private boolean mostrarEmail;
    private boolean mostrarNombreReal;
    private boolean mostrarApellidos;
    private boolean mostrarUbicacion;
    private boolean mostrarDescripcionVendedor;
    private boolean mostrarDescripcionRadio;
    private boolean mostrarActivoDesde;
}
