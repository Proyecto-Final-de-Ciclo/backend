package com.example.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Reseña;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.ReseñaRequestDto;
import com.example.demo.dto.ReseñaResponseDto;
import com.example.demo.dto.UsuarioPublicoDto;
import com.example.demo.repositories.ReseñaRepository;

@Service
public class ReseñaService {

    @Autowired
    private ReseñaRepository reseñaRepositorio;

    @Autowired
    private UsuarioService usuarioService;

    // convierte una reseña a reseñarespondedto, con los datos del autor que
    // interesan.
    private ReseñaResponseDto toDto(Reseña reseña) {
        UsuarioPublicoDto autorDto = new UsuarioPublicoDto(
                reseña.getAutor().getId(),
                reseña.getAutor().getNombre(),
                reseña.getAutor().getRol().name(),
                reseña.getAutor().getFechaRegistro(),
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                false,
                false,
                false,
                false,
                false,
                false,
                false);

        return new ReseñaResponseDto(
                reseña.getId(),
                reseña.getPuntuacion(),
                reseña.getComentario(),
                reseña.getFecha(),
                autorDto);
    }

    // lista reseñas de un vendedor, por fecha descendente.
    public List<ReseñaResponseDto> obtenerPorVendedor(Long vendedorId) {
        List<Reseña> reseñas = reseñaRepositorio.findByVendedorIdOrderByFechaDesc(vendedorId);
        List<ReseñaResponseDto> resultado = new ArrayList<>();
        for (Reseña reseña : reseñas) {
            resultado.add(toDto(reseña));
        }
        return resultado;
    }

    // obtiene autor y vendedor, valida que no sea reseña de él mismo, crea reseña
    // de hoy y guarda.
    public ReseñaResponseDto crear(Long vendedorId, ReseñaRequestDto dto) {
        Usuario autor = usuarioService.obtenerUsuarioConectado();
        Usuario vendedor = usuarioService.obtenerPorId(vendedorId);

        if (vendedor == null) {
            throw new IllegalArgumentException("Vendedor no encontrado");
        }

        if (autor.getId().equals(vendedor.getId())) {
            throw new IllegalArgumentException("No puedes reseñarte a ti mismo");
        }

        Reseña reseña = new Reseña(
                null,
                dto.getPuntuacion(),
                dto.getComentario(),
                LocalDate.now(),
                autor,
                vendedor);

        return toDto(reseñaRepositorio.save(reseña));
    }

    // Puede borrar la reseña el autor o el admin
    public void borrar(Long reseñaId) {
        Reseña reseña = reseñaRepositorio.findById(reseñaId).orElse(null);
        if (reseña == null) {
            throw new IllegalArgumentException("Reseña no encontrada");
        }

        Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
        boolean esAutor = reseña.getAutor().getId().equals(usuarioConectado.getId());
        boolean esAdmin = usuarioConectado.getRol() == Rol.ADMIN;

        if (!esAutor && !esAdmin) {
            throw new SecurityException("No tienes permiso para borrar esta reseña");
        }

        reseñaRepositorio.deleteById(reseñaId);
    }

    public Double calcularMedia(Long vendedorId) {
        return reseñaRepositorio.calcularMediaPorVendedor(vendedorId);
    }

    public int contarReseñas(Long vendedorId) {
        return reseñaRepositorio.countByVendedorId(vendedorId);
    }
}
