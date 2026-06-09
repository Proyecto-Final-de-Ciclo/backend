package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Favorito;
import com.example.demo.domain.LlamadaQso;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.PerfilRequestDto;
import com.example.demo.repositories.AnuncioRepository;
import com.example.demo.repositories.FavoritoRepository;
import com.example.demo.repositories.LlamadaQsoRepository;
import com.example.demo.repositories.ReseñaRepository;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private FavoritoRepository favoritoRepositorio;

    @Autowired
    private ReseñaRepository reseñaRepositorio;

    @Autowired
    private LlamadaQsoRepository llamadaQsoRepositorio;

    @Autowired
    private AnuncioRepository anuncioRepositorio;

    @Autowired
    @Lazy
    private AnuncioService anuncioService;

    public Usuario obtenerPorId(long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // al añadir se encripta la contraseña con BCrypt
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

    @Transactional
    public void borrar(Long id) {
        Usuario usuario = this.obtenerPorId(id);
        if (usuario == null) return;

        List<Favorito> favoritos = favoritoRepositorio.findByUsuario(usuario);
        favoritoRepositorio.deleteAll(favoritos);

        reseñaRepositorio.deleteByAutor(usuario);
        reseñaRepositorio.deleteByVendedor(usuario);

        List<LlamadaQso> llamadas = llamadaQsoRepositorio.findByUsuario(usuario);
        llamadaQsoRepositorio.deleteAll(llamadas);

        List<Anuncio> anuncios = anuncioRepositorio.findByUsuario(usuario);
        for (Anuncio anuncio : anuncios) {
            anuncioService.borrar(anuncio.getId());
        }

        usuarioRepository.delete(usuario);
    }

    public Usuario obtenerUsuarioConectado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String nombreUsuarioContectado = authentication.getName();
            return usuarioRepository.findByNombre(nombreUsuarioContectado);
        }
        return null;
    }


    // si no hay busqueda devuelve todos si la hay busca por nombre, email o indicativo.
    public List<Usuario> buscar(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.buscarPorNombreEmailOIndicativo(busqueda);
    }

    // eidtar sin tocar la contraseña. lo usa el admin
    public Usuario editarSinPassword(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ya existe un usuario con ese nombre o email");
        }
    }

    // edita el perfil, validando el indicativo y pasandolo a mayusculas. No toca la
    // contraseña.
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

    // comprueba la contraseña actual, valida que la nueva tenga más de 6
    // caracteres, la cifra y la guarda.
    public void cambiarPassword(String passwordActual, String passwordNueva) {
        Usuario usuario = obtenerUsuarioConectado();
        if (usuario == null) {
            throw new RuntimeException("No hay usuario conectado");
        }

        if (passwordActual == null || !passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        if (passwordNueva == null || passwordNueva.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}
