package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRequestDto {

    private String descripcion;
    private String indicativo;
    private String localizacion;
    private boolean mostrarEmail;

    private String nombre;
    private String nombreReal;
    private String apellidos;

    private String activoDesde;
    private String modos;
    private boolean qslBuro;
    private String descripcionRadio;

    private boolean mostrarNombreReal;
    private boolean mostrarApellidos;
    private boolean mostrarUbicacion;
    private boolean mostrarDescripcionVendedor;
    private boolean mostrarDescripcionRadio;
    private boolean mostrarActivoDesde;
}