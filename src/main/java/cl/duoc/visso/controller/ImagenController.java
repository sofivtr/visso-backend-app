package cl.duoc.visso.controller;

import cl.duoc.visso.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = "*")
public class ImagenController {

    private final FileStorageService fileStorageService;

    public ImagenController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> subirImagen(@RequestParam("imagen") MultipartFile file,
                                         @RequestParam(value = "categoria", required = false, defaultValue = "GENERAL") String categoria) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            // Usar el FileStorageService para guardar la imagen
            String imagenUrl = fileStorageService.storeProductImage(file, categoria);

            // Retornar la URL relativa de la imagen
            Map<String, String> response = new HashMap<>();
            response.put("imagenUrl", imagenUrl);
            response.put("mensaje", "Imagen subida exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al guardar la imagen: " + e.getMessage()));
        }
    }
}
