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
import com.example.demo.dto.CambioPasswordDto;
import com.example.demo.dto.MessageResponse;
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

    // LISTA USUARIOS para el panel de admin
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

    // EDICIÓN DE UN USUARIO para el panel admin. permite cambiar nombre, email y
    // rol.
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

    // BORRAR USUARIO panel admin
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Usuario existente = usuarioService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.borrar(id);
        return ResponseEntity.noContent().build();
    }

    // PERFIL PÚBLICO USUARIO. si es el del usuario conectado devuelve todo,
    // si no solo lo que se quiere mostrar.
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> getPerfilPublico(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null)
            return ResponseEntity.notFound().build();
        Usuario conectado = usuarioService.obtenerUsuarioConectado();
        boolean esPropietario = conectado != null && conectado.getId().equals(id);
        return ResponseEntity.ok(toDto(usuario, esPropietario));
    }

    // ANUNCIOS DE UN USUARIO. para ver los anuncios de un usuario desde su perfil
    @GetMapping("/usuario/{id}/anuncios")
    public ResponseEntity<?> getAnunciosDeUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null)
            return ResponseEntity.notFound().build();
        List<AnuncioConImagenesDto> anuncios = anuncioService.obtenerPorUsuarioConImagenes(usuario);
        return ResponseEntity.ok(anuncios);
    }

    // EDITAR EL PROPIO PERFIL.
    @PutMapping("/usuario/perfil")
    public ResponseEntity<?> editarPerfil(@RequestBody PerfilRequestDto dto) {
        dto.setDescripcion(SanitizerUtil.sanitize(dto.getDescripcion(), ""));
        dto.setIndicativo(SanitizerUtil.sanitize(dto.getIndicativo(), ""));
        dto.setLocalizacion(SanitizerUtil.sanitize(dto.getLocalizacion(), ""));
        Usuario actualizado = usuarioService.editarPerfil(dto);
        return ResponseEntity.ok(toDto(actualizado, true));
    }

    // CAMIBAR CONTRASEÑA
    @PutMapping("/usuario/password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambioPasswordDto dto) {
        try {
            usuarioService.cambiarPassword(dto.getPasswordActual(), dto.getPasswordNueva());
            return ResponseEntity.ok(new MessageResponse("Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // oculta campos privados
    private UsuarioPublicoDto toDto(Usuario u) {
        return toDto(u, false);
    }


    // versión completa para el propietario del perfil. Calcula media de reseñas.
    private UsuarioPublicoDto toDto(Usuario u, boolean incluirPrivados) {
        return new UsuarioPublicoDto(
                u.getId(),
                u.getNombre(),
                u.getRol().name(),
                u.getFechaRegistro(),
                reseñaService.calcularMedia(u.getId()),
                reseñaService.contarReseñas(u.getId()),
                (incluirPrivados || u.isMostrarDescripcionVendedor()) ? u.getDescripcion() : null,
                u.getIndicativo(),
                (incluirPrivados || u.isMostrarUbicacion()) ? u.getLocalizacion() : null,
                (incluirPrivados || u.isMostrarEmail()) ? u.getEmail() : null,
                (incluirPrivados || u.isMostrarNombreReal()) ? u.getNombreReal() : null,
                (incluirPrivados || u.isMostrarApellidos()) ? u.getApellidos() : null,
                (incluirPrivados || u.isMostrarActivoDesde()) ? u.getActivoDesde() : null,
                u.getModos(),
                u.isQslBuro(),
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