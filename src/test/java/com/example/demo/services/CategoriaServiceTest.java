// TEST UNITARIOS
package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.Categoria;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    public void init() {
        categoria = new Categoria(null, "CategoriaTest");
    }

    // obtener una categoría que existe
    @Test
    public void obtenerPorIdTest_ok() {
        Categoria resultado = categoriaService.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals("Emisoras", resultado.getNombre());
    }

    // añadir una categoría con nombre duplicado lanza excepción
    @Test
    public void añadirCategoriaTest_duplicada() {
        categoriaService.añadir(categoria);
        assertThrows(RuntimeException.class, () -> {
            categoriaService.añadir(categoria);
        });
    }

    // editar una categoría con id inexistente lanza excepción
    @Test
    public void editarCategoriaTest_noExiste() {
        categoria.setId(999999L);
        assertThrows(RuntimeException.class, () -> {
            categoriaService.editar(categoria);
        });
    }
}