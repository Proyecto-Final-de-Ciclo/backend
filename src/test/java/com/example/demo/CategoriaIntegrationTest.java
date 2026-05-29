// TEST DE INTEGRACION
package com.example.demo;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.domain.Categoria;
import com.example.demo.repositories.CategoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class CategoriaIntegrationTest {

    @MockitoBean
    private CategoriaRepository categoriaRepository;

    @Autowired
    MockMvc mockMvc;

    List<Categoria> mockList;
    Categoria categoriaSinId;
    Categoria categoriaConId;

    @BeforeAll
    void initTest() {
        mockList = new ArrayList<>();
        mockList.add(new Categoria(1L, "Emisoras"));
        mockList.add(new Categoria(2L, "Antenas"));

        categoriaSinId = new Categoria(null, "Walkie Talkies");
        categoriaConId = new Categoria(3L, "Walkie Talkies");
    }

    @Test
    public void getAllCategoriasTest() throws Exception {
        when(categoriaRepository.findAll()).thenReturn(mockList);

        mockMvc.perform(get("/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Emisoras")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Antenas")));
    }

    @Test
    public void getOneCategoriaTest() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(mockList.get(0)));

        mockMvc.perform(get("/categoria/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Emisoras")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addCategoriaTest() throws Exception {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaConId);

        mockMvc.perform(post("/categoria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(categoriaSinId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Walkie Talkies")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteOneCategoriaTest() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(mockList.get(0)));

        mockMvc.perform(delete("/categoria/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}