package cl.duoc.visso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = "*")
public class ImagenController {

    // Ruta donde se guardarán las imágenes (relativa al proyecto)
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @PostMapping("/upload")
    public ResponseEntity<?> subirImagen(@RequestParam("imagen") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo debe ser una imagen"));
            }

            // Generar nombre único para el archivo
            String extension = getFileExtension(file.getOriginalFilename());
            String nombreArchivo = "producto_" + UUID.randomUUID().toString() + extension;

            // Crear el directorio si no existe
            File uploadPath = new File(UPLOAD_DIR);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Guardar el archivo
            Path path = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Retornar la URL relativa de la imagen
            Map<String, String> response = new HashMap<>();
            response.put("imagenUrl", "/images/" + nombreArchivo);
            response.put("mensaje", "Imagen subida exitosamente");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al guardar la imagen: " + e.getMessage()));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
