package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity

public class Imagen {
    @Id
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private Long id;

    // no es una URL completa, es el nombre del archivo
    // y el frontend la compone como ${API}/files/${imagen.url}
    private String url;
    private boolean esPrincipal;
    @ManyToOne
    private Anuncio anuncio;
}