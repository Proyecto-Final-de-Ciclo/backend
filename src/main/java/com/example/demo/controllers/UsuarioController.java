package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.AnuncioConImagenesDto;
import com.example.demo.dto.PerfilRequestDto;
import com.example.demo.dto.UsuarioPublicoDto;
import com.example.demo.services.AnuncioService;
import com.example.demo.services.ReseñaService;
import com.example.demo.services.UsuarioService;
import com.example.demo.utils.SanitizerUtil;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private ReseñaService reseñaService;

    @GetMapping("/usuarios")
    public ResponseEntity<?> showList(@RequestParam(required = false) String busqueda) {
        List<Usuario> usuarios = usuarioService.buscar(busqueda);
        if (usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UsuarioPublicoDto> dtos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            dtos.add(toDto(usuario));
        }
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<?> editUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario existente = usuarioService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        usuario.setNombre(SanitizerUtil.sanitize(usuario.getNombre(), "usuario"));
        existente.setNombre(usuario.getNombre());
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
            existente.setEmail(usuario.getEmail());
        }
        existente.setRol(usuario.getRol());
        Usuario actualizado = usuarioService.editarSinPassword(existente);
        return ResponseEntity.ok(toDto(actualizado));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Usuario existente = usuarioService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.borrar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> getPerfilPublico(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null)
            return ResponseEntity.notFound().build();
        Usuario conectado = usuarioService.obtenerUsuarioConectado();
        boolean esPropietario = conectado != null && conectado.getId().equals(id);
        return ResponseEntity.ok(toDto(usuario, esPropietario));
    }

    @GetMapping("/usuario/{id}/anuncios")
    public ResponseEntity<?> getAnunciosDeUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null)
            return ResponseEntity.notFound().build();
        List<AnuncioConImagenesDto> anuncios = anuncioService.obtenerPorUsuarioConImagenes(usuario);
        return ResponseEntity.ok(anuncios);
    }

    @PutMapping("/usuario/perfil")
    public ResponseEntity<?> editarPerfil(@RequestBody PerfilRequestDto dto) {
        dto.setDescripcion(SanitizerUtil.sanitize(dto.getDescripcion(), ""));
        dto.setIndicativo(SanitizerUtil.sanitize(dto.getIndicativo(), ""));
        dto.setLocalizacion(SanitizerUtil.sanitize(dto.getLocalizacion(), ""));
        Usuario actualizado = usuarioService.editarPerfil(dto);
        return ResponseEntity.ok(toDto(actualizado, true));
    }

    private UsuarioPublicoDto toDto(Usuario u) {
        return toDto(u, false); // por defecto, versión pública (filtrada)
    }

    private UsuarioPublicoDto toDto(Usuario u, boolean incluirPrivados) {
        return new UsuarioPublicoDto(
                u.getId(),
                u.getNombre(),
                u.getRol().name(),
                u.getFechaRegistro(),
                reseñaService.calcularMedia(u.getId()),
                reseñaService.contarReseñas(u.getId()),
                (incluirPrivados || u.isMostrarDescripcionVendedor()) ? u.getDescripcion() : null,
                u.getIndicativo(), // siempre público
                (incluirPrivados || u.isMostrarUbicacion()) ? u.getLocalizacion() : null,
                (incluirPrivados || u.isMostrarEmail()) ? u.getEmail() : null,
                (incluirPrivados || u.isMostrarNombreReal()) ? u.getNombreReal() : null,
                (incluirPrivados || u.isMostrarApellidos()) ? u.getApellidos() : null,
                (incluirPrivados || u.isMostrarActivoDesde()) ? u.getActivoDesde() : null,
                u.getModos(), // siempre público
                u.isQslBuro(), // siempre público
                (incluirPrivados || u.isMostrarDescripcionRadio()) ? u.getDescripcionRadio() : null,
                u.isMostrarEmail(),
                u.isMostrarNombreReal(),
                u.isMostrarApellidos(),
                u.isMostrarUbicacion(),
                u.isMostrarDescripcionVendedor(),
                u.isMostrarDescripcionRadio(),
                u.isMostrarActivoDesde());
    }
}