// TEST DE SERVICIO
package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Categoria;
import com.example.demo.domain.Estado;
import com.example.demo.repositories.AnuncioRepository;
import com.example.demo.repositories.FavoritoRepository;
import com.example.demo.repositories.ImagenRepository;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AnuncioServiceTest {

    @InjectMocks
    AnuncioService anuncioService;

    @Mock
    AnuncioRepository anuncioRepositorio;
    @Mock
    ImagenRepository imagenRepositorio;
    @Mock
    FavoritoRepository favoritoRepositorio;
    @Mock
    FileStorageService fileStorageService;
    @Mock
    ReseñaService reseñaService;

    ArrayList<Anuncio> mockList;

    @BeforeAll
    public void init() {
        Categoria cat = new Categoria(1L, "Emisoras");

        mockList = new ArrayList<>();
        mockList.add(Anuncio.builder().id(1L).nombre("Yaesu FT-857D").precio(350.0)
                .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                .descripcion("Transceptor multibanda").categoria(cat).build());
        mockList.add(Anuncio.builder().id(2L).nombre("Icom IC-7300").precio(900.0)
                .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                .descripcion("Emisora HF").categoria(cat).build());
        mockList.add(Anuncio.builder().id(3L).nombre("Baofeng UV-5R").precio(25.0)
                .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                .descripcion("Walkie bibanda").categoria(cat).build());
    }

    // obtener un anuncio por id existente devuelve el anuncio correcto
    @Test
    public void obtenerPorIdTest_ok() {
        when(anuncioRepositorio.findById(1L)).thenReturn(Optional.of(mockList.get(0)));

        Anuncio resultado = anuncioService.obtenerPorId(1L);

        assertEquals("Yaesu FT-857D", resultado.getNombre());
        assertEquals(350.0, resultado.getPrecio());
        verify(anuncioRepositorio, times(1)).findById(1L);
    }

    // obtener un anuncio por id inexistente devuelve null
    @Test
    public void obtenerPorIdTest_noExiste() {
        when(anuncioRepositorio.findById(999L)).thenReturn(Optional.empty());

        Anuncio resultado = anuncioService.obtenerPorId(999L);

        assertNull(resultado);
        verify(anuncioRepositorio, times(1)).findById(999L);
    }

    // añadir un anuncio llama a save y devuelve el anuncio guardado
    @Test
    public void añadirTest_ok() {
        Anuncio nuevo = mockList.get(0);
        when(anuncioRepositorio.save(nuevo)).thenReturn(nuevo);

        Anuncio guardado = anuncioService.añadir(nuevo);

        assertEquals("Yaesu FT-857D", guardado.getNombre());
        verify(anuncioRepositorio, times(1)).save(nuevo);
    }

    // editar un anuncio con id inexistente lanza excepción y no llama a save
    @Test
    public void editarTest_noExiste() {
        Anuncio anuncio = mockList.get(0);
        when(anuncioRepositorio.findById(anuncio.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            anuncioService.editar(anuncio);
        });
        verify(anuncioRepositorio, times(0)).save(anuncio);
    }

    // borrar un anuncio existente llama a deleteById
    @Test
    public void borrarTest_ok() {
        Anuncio anuncio = mockList.get(0);
        when(anuncioRepositorio.findById(1L)).thenReturn(Optional.of(anuncio));
        when(imagenRepositorio.findByAnuncio(anuncio)).thenReturn(new ArrayList<>());

        anuncioService.borrar(1L);

        verify(anuncioRepositorio, times(1)).deleteById(1L);
        verify(favoritoRepositorio, times(1)).deleteByAnuncio(anuncio);
    }
}