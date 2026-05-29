package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploadDir");

    private static final List<String> EXTENSIONES_PERMITIDAS = List.of("jpg", "jpeg", "png", "gif", "webp");

    public String store(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("archivo enviado vacío");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..")) {
            throw new Exception("nombre de archivo incorrecto");
        }
        
        String extension = StringUtils.getFilenameExtension(filename).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new Exception("Solo se permiten imágenes (jpg, jpeg, png, gif, webp)");
        }

        String storedFilename = System.currentTimeMillis() + "." + extension;

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException ioe) {
            throw new Exception("Error al almacenar el archivo");
        }
    }

    public void delete(String filename) throws RuntimeException {
        try {
            Path file = rootLocation.resolve(filename);
            if (!Files.exists(file))
                throw new RuntimeException("No existe el fichero");
            Files.delete(file);
        } catch (IOException ioe) {
            throw new RuntimeException("Error en borrado");
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            System.err.println("Error IO");
        }
        return null;
    }

    public List<String> storeMultiple(List<MultipartFile> files) throws Exception {
        List<String> nombres = new ArrayList<>();
        for (MultipartFile file : files) {
            nombres.add(store(file));
        }
        return nombres;
    }
}