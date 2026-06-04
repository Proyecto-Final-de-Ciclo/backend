package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.PerfilRequestDto;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario añadir(Usuario usuario) {
        String passCrypted = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passCrypted);
        try {
            usuario.setFechaRegistro(LocalDate.now());
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ya existe un usuario con ese nombre o email");
        }
    }

    public Usuario editar(Usuario usuario) {
        String passCrypted = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passCrypted);
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ya existe un usuario con ese nombre o email");
        }
    }

    public void borrar(Long id) {
        Usuario usuario = this.obtenerPorId(id);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    public Usuario obtenerUsuarioConectado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String nombreUsuarioContectado = authentication.getName();
            return usuarioRepository.findByNombre(nombreUsuarioContectado);
        }
        return null;
    }

    public List<Usuario> buscar(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.buscarPorNombreEmailOIndicativo(busqueda);
    }

    public Usuario editarSinPassword(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ya existe un usuario con ese nombre o email");
        }
    }

    public Usuario editarPerfil(PerfilRequestDto dto) {
        Usuario usuario = obtenerUsuarioConectado();

        if (dto.getIndicativo() != null && !dto.getIndicativo().isBlank()) {
            if (!dto.getIndicativo().matches("^[A-Za-z]{1,3}\\d[A-Za-z]{1,4}$")) {
                throw new RuntimeException("El indicativo no tiene un formato válido (ej: EA1IWS).");
            }
        }

        usuario.setDescripcion(dto.getDescripcion());
        usuario.setIndicativo(dto.getIndicativo() != null ? dto.getIndicativo().toUpperCase() : null);
        usuario.setLocalizacion(dto.getLocalizacion());
        usuario.setMostrarEmail(dto.isMostrarEmail());

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }

        usuario.setNombreReal(dto.getNombreReal());
        usuario.setApellidos(dto.getApellidos());

        usuario.setActivoDesde(dto.getActivoDesde());
        usuario.setModos(dto.getModos());
        usuario.setQslBuro(dto.isQslBuro());
        usuario.setDescripcionRadio(dto.getDescripcionRadio());

        usuario.setMostrarNombreReal(dto.isMostrarNombreReal());
        usuario.setMostrarApellidos(dto.isMostrarApellidos());
        usuario.setMostrarUbicacion(dto.isMostrarUbicacion());
        usuario.setMostrarDescripcionVendedor(dto.isMostrarDescripcionVendedor());
        usuario.setMostrarDescripcionRadio(dto.isMostrarDescripcionRadio());
        usuario.setMostrarActivoDesde(dto.isMostrarActivoDesde());

        return editarSinPassword(usuario);
    }
}
