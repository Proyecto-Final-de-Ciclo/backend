package com.example.demo.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Estado;
import com.example.demo.domain.Imagen;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.AnuncioConImagenesDto;
import com.example.demo.dto.MessageResponse;
import com.example.demo.services.AnuncioService;
import com.example.demo.services.FileStorageService;
import com.example.demo.services.IaService;
import com.example.demo.services.ImagenService;
import com.example.demo.services.UsuarioService;
import com.example.demo.utils.SanitizerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class AnuncioController {

	@Autowired
	private AnuncioService anuncioService;

	@Autowired
	private ImagenService imagenService;

	@Autowired
	public FileStorageService fileStorageService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private IaService iaService;

	@GetMapping("/anuncios")
	public ResponseEntity<?> showList(
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) Estado estado,
			@RequestParam(required = false) Double precioMin,
			@RequestParam(required = false) Double precioMax,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size) {

		if (precioMin != null && precioMax != null && precioMin > precioMax) {
			return ResponseEntity.badRequest().build();
		}

		Pageable pageable = PageRequest.of(page, size);

		Page<AnuncioConImagenesDto> resultado = anuncioService.obtenerTodosConImagenes(
				nombre, categoriaId, estado, precioMin, precioMax, pageable);

		if (resultado.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(resultado);
	}

	@GetMapping({ "/anuncio/{id}" })
	public ResponseEntity<?> showElement(@PathVariable long id) {
		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(anuncio);
		}
	}

	@GetMapping("/anuncio/{id}/imagenes")
	public ResponseEntity<?> getImagenes(@PathVariable Long id) {
		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null) {
			return ResponseEntity.notFound().build();
		}
		List<Imagen> imagenes = imagenService.obtenerPorAnuncio(anuncio);
		return ResponseEntity.ok().body(imagenes);
	}

	@PostMapping(value = "/anuncio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> newElement(
			@RequestPart("data") @Valid Anuncio nuevoAnuncio,
			@RequestPart("files") List<MultipartFile> files,
			@RequestPart("principal") String principalStr) {

		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		if (usuarioConectado.getRol() != Rol.USER) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		if (files != null && files.size() > 6) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("No se pueden subir más de 6 imágenes (1 principal + 5 secundarias)");
		}
		nuevoAnuncio.setNombre(SanitizerUtil.sanitize(nuevoAnuncio.getNombre(), "producto sin nombre"));
		nuevoAnuncio.setDescripcion(SanitizerUtil.sanitize(nuevoAnuncio.getDescripcion(), null));
		nuevoAnuncio.setUsuario(usuarioConectado);
		nuevoAnuncio.setFechaPublicacion(LocalDate.now());
		Anuncio anuncio = anuncioService.añadir(nuevoAnuncio);

		if (files != null && !files.isEmpty()) {
			try {
				int principalIndex = Integer.parseInt(principalStr);
				List<String> nombres = fileStorageService.storeMultiple(files);
				for (int i = 0; i < nombres.size(); i++) {
					Imagen imagen = new Imagen(null, nombres.get(i), i == principalIndex, anuncio);
					imagenService.añadir(imagen);
				}
			} catch (Exception e) {
				anuncioService.borrar(anuncio.getId());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error al subir las imágenes");
			}
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(anuncio);
	}

	@PutMapping({ "/anuncio/{id}" })
	public ResponseEntity<?> showEdit(@Valid @RequestBody Anuncio anuncio, @PathVariable Long id) {
		Anuncio editarAnuncio = anuncioService.obtenerPorId(id);
		if (editarAnuncio == null) {
			return ResponseEntity.notFound().build();
		}
		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		boolean esDueno = editarAnuncio.getUsuario() != null
				&& editarAnuncio.getUsuario().getId().equals(usuarioConectado.getId());

		if (!esDueno) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
		}
		anuncio.setNombre(SanitizerUtil.sanitize(anuncio.getNombre(), "producto sin nombre"));
		anuncio.setDescripcion(SanitizerUtil.sanitize(anuncio.getDescripcion(), null));
		anuncio.setUsuario(editarAnuncio.getUsuario());
		editarAnuncio = anuncioService.editar(anuncio);
		return ResponseEntity.status(HttpStatus.OK).body(editarAnuncio);
	}

	@PutMapping(value = "/anuncio/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addImagenes(
			@PathVariable Long id,
			@RequestPart("files") List<MultipartFile> files,
			@RequestPart("principal") String principalStr) {

		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null)
			return ResponseEntity.notFound().build();

		try {
			Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
			boolean esAdmin = usuarioConectado.getRol() == Rol.ADMIN;
			boolean esDueno = anuncio.getUsuario() != null
					&& anuncio.getUsuario().getId().equals(usuarioConectado.getId());
			if (!esAdmin && !esDueno) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			int principalIndex = Integer.parseInt(principalStr);
			if (principalIndex >= 0) {
				List<Imagen> imagenesActuales = imagenService.obtenerPorAnuncio(anuncio);
				for (Imagen img : imagenesActuales) {
					img.setEsPrincipal(false);
					imagenService.añadir(img);
				}
			}
			int imagenesActuales = imagenService.obtenerPorAnuncio(anuncio).size();
			if (imagenesActuales + files.size() > 6) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("No se pueden subir más de 6 imágenes en total (1 principal + 5 secundarias)");
			}
			List<String> nombres = fileStorageService.storeMultiple(files);
			for (int i = 0; i < nombres.size(); i++) {
				Imagen imagen = new Imagen(null, nombres.get(i), i == principalIndex, anuncio);
				imagenService.añadir(imagen);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al subir las imágenes");
		}

		return ResponseEntity.status(HttpStatus.OK).body(anuncio);
	}

	@PutMapping("/anuncio/{id}/imagenes/{imagenId}/principal")
	public ResponseEntity<?> setPrincipal(
			@PathVariable Long id,
			@PathVariable Long imagenId) {

		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null) {
			return ResponseEntity.notFound().build();
		}
		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		boolean esAdmin = usuarioConectado.getRol() == Rol.ADMIN;
		boolean esDueno = anuncio.getUsuario() != null
				&& anuncio.getUsuario().getId().equals(usuarioConectado.getId());

		if (!esAdmin && !esDueno) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		List<Imagen> imagenes = imagenService.obtenerPorAnuncio(anuncio);
		for (Imagen img : imagenes) {
			img.setEsPrincipal(false);
			imagenService.añadir(img);
		}

		Imagen imagen = imagenService.obtenerPorId(imagenId);
		if (imagen == null)
			return ResponseEntity.notFound().build();
		imagen.setEsPrincipal(true);
		imagenService.añadir(imagen);

		return ResponseEntity.ok().body(imagen);
	}

	@DeleteMapping({ "/anuncio/{id}" })
	public ResponseEntity<?> showDelete(@PathVariable long id) {
		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null) {
			return ResponseEntity.notFound().build();
		}
		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		boolean esAdmin = usuarioConectado.getRol() == Rol.ADMIN;
		boolean esDueno = anuncio.getUsuario() != null
				&& anuncio.getUsuario().getId().equals(usuarioConectado.getId());

		if (!esAdmin && !esDueno) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
		}
		anuncioService.borrar(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/anuncio/{id}/imagenes/{imagenId}")
	public ResponseEntity<?> deleteImagen(
			@PathVariable Long id,
			@PathVariable Long imagenId) {

		// comprobamos que existe el anuncio
		Anuncio anuncio = anuncioService.obtenerPorId(id);
		if (anuncio == null) {
			return ResponseEntity.notFound().build();
		}

		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		boolean esAdmin = usuarioConectado.getRol() == Rol.ADMIN;
		boolean esDueno = anuncio.getUsuario() != null
				&& anuncio.getUsuario().getId().equals(usuarioConectado.getId());

		if (!esAdmin && !esDueno) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		// comprobamos que existe la imagen
		Imagen imagen = imagenService.obtenerPorId(imagenId);
		if (imagen == null) {
			return ResponseEntity.notFound().build();
		}

		try {
			// borramos la imagen del servidor
			fileStorageService.delete(imagen.getUrl());
			// borramos la imagen de la base de datos
			imagenService.borrar(imagenId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al borrar la imagen");
		}

		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/files/{filename:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
		Resource file = fileStorageService.loadAsResource(filename);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
		} catch (IOException ex) {
			System.err.println("No se puede determinar el tipo de archivo.");
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(file);
	}

	@GetMapping("/anuncios/mios")
	public ResponseEntity<?> showMisAnuncios() {
		Usuario usuarioConectado = usuarioService.obtenerUsuarioConectado();
		if (usuarioConectado == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		List<AnuncioConImagenesDto> anuncios = anuncioService.obtenerPorUsuarioConImagenes(usuarioConectado);
		return ResponseEntity.ok(anuncios);
	}

	@GetMapping("/anuncio/ia/descripcion")
	public ResponseEntity<?> generarDescripcion(@RequestParam String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return ResponseEntity.badRequest().body(new MessageResponse("El nombre es obligatorio"));
		}
		String descripcion = iaService.generarDescripcion(nombre);
		return ResponseEntity.ok(new MessageResponse(descripcion));
	}
}