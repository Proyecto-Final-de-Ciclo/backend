package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Estado;
import com.example.demo.domain.Imagen;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.AnuncioConImagenesDto;
import com.example.demo.dto.UsuarioPublicoDto;
import com.example.demo.repositories.AnuncioRepository;
import com.example.demo.repositories.FavoritoRepository;
import com.example.demo.repositories.ImagenRepository;

@Service
@Primary
public class AnuncioService {
    @Autowired
    private AnuncioRepository anuncioRepositorio;

    @Autowired
    private ImagenRepository imagenRepositorio;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FavoritoRepository favoritoRepositorio;

    @Autowired
    private ReseñaService reseñaService;

    public Anuncio obtenerPorId(Long id) {
        return anuncioRepositorio.findById(id).orElse(null);
    }

    public Anuncio añadir(Anuncio anuncio) {
        return anuncioRepositorio.save(anuncio);
    }

    public Anuncio editar(Anuncio anuncio) {
        if (anuncioRepositorio.findById(anuncio.getId()).isEmpty()) {
            throw new RuntimeException("Anuncio no encontrado con id: " + anuncio.getId());
        }
        return anuncioRepositorio.save(anuncio);
    }

    // borramos también imagenes y faovirtos
    public void borrar(Long id) {
        Anuncio anuncio = obtenerPorId(id);
        if (anuncio == null)
            return;
        List<Imagen> imagenes = imagenRepositorio.findByAnuncio(anuncio);
        for (Imagen imagen : imagenes) {
            fileStorageService.delete(imagen.getUrl());
        }
        imagenRepositorio.deleteAll(imagenes);
        favoritoRepositorio.deleteByAnuncio(anuncio);
        anuncioRepositorio.deleteById(id);
    }

    public AnuncioConImagenesDto toDto(Anuncio anuncio) {
        List<Imagen> imagenes = imagenRepositorio.findByAnuncio(anuncio);
        Usuario u = anuncio.getUsuario();
        Long uid = u.getId();

        UsuarioPublicoDto usuarioPublico = new UsuarioPublicoDto(
                uid,
                u.getNombre(),
                u.getRol().name(),
                u.getFechaRegistro(),
                reseñaService.calcularMedia(uid),
                reseñaService.contarReseñas(uid),
                u.getDescripcion(),
                u.getIndicativo(),
                u.getLocalizacion(),
                u.isMostrarEmail() ? u.getEmail() : null,
                u.isMostrarNombreReal() ? u.getNombreReal() : null,
                u.isMostrarApellidos() ? u.getApellidos() : null,
                u.getActivoDesde(),
                u.getModos(),
                u.isQslBuro(),
                u.isMostrarDescripcionRadio() ? u.getDescripcionRadio() : null,
                u.isMostrarEmail(),
                u.isMostrarNombreReal(),
                u.isMostrarApellidos(),
                u.isMostrarUbicacion(),
                u.isMostrarDescripcionVendedor(),
                u.isMostrarDescripcionRadio(),
                u.isMostrarActivoDesde());

        return new AnuncioConImagenesDto(
                anuncio.getId(),
                anuncio.getNombre(),
                anuncio.getPrecio(),
                anuncio.getEstado(),
                anuncio.getFechaPublicacion(),
                anuncio.getDescripcion(),
                anuncio.getCategoria(),
                usuarioPublico,
                imagenes);
    }

    public Page<AnuncioConImagenesDto> obtenerTodosConImagenes(
            String nombre, Long categoriaId,
            Estado estado, Double precioMin, Double precioMax,
            Pageable pageable) {

        Page<Anuncio> pagina = anuncioRepositorio.findConFiltros(nombre, categoriaId, estado, precioMin, precioMax,
                pageable);

        return pagina.map(anuncio -> toDto(anuncio));
    }

    public List<AnuncioConImagenesDto> obtenerPorUsuarioConImagenes(Usuario usuario) {
        List<Anuncio> anuncios = anuncioRepositorio.findByUsuario(usuario);
        List<AnuncioConImagenesDto> resultado = new ArrayList<>();
        for (Anuncio anuncio : anuncios) {
            resultado.add(toDto(anuncio));
        }
        return resultado;
    }
}