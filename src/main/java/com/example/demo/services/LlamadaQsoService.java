package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.domain.LlamadaQso;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.LlamadaQsoRepository;

@Service
public class LlamadaQsoService {

    @Autowired
    private LlamadaQsoRepository llamadaQsoRepository;

    public List<LlamadaQso> obtenerActivas() {
        return llamadaQsoRepository
                .findByExpiraEnAfterOrderByFechaPublicacionDesc(LocalDateTime.now());
    }

    public LlamadaQso publicar(LlamadaQso llamada, int minutosExpiracion) {
        List<LlamadaQso> anteriores = llamadaQsoRepository.findByUsuario(llamada.getUsuario());
        llamadaQsoRepository.deleteAll(anteriores);

        llamada.setFechaPublicacion(LocalDateTime.now());
        llamada.setExpiraEn(LocalDateTime.now().plusMinutes(minutosExpiracion));
        return llamadaQsoRepository.save(llamada);
    }

    public LlamadaQso obtenerPorId(Long id) {
        return llamadaQsoRepository.findById(id).orElse(null);
    }

    public void borrar(Long id) {
        llamadaQsoRepository.deleteById(id);
    }

    public List<LlamadaQso> obtenerPorUsuario(Usuario usuario) {
        return llamadaQsoRepository.findByUsuario(usuario);
    }

    @Scheduled(fixedRate = 300000)
    public void limpiarExpiradas() {
        List<LlamadaQso> expiradas = llamadaQsoRepository
                .findByExpiraEnBefore(LocalDateTime.now());
        llamadaQsoRepository.deleteAll(expiradas);
    }
}