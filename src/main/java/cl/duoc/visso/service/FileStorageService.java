package cl.duoc.visso.service;

import cl.duoc.visso.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

@Service
public class FileStorageService {

    private final Path uploadLocation;
    private static final List<String> ALLOWED_EXTENSIONS = 
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "avif");
    
    public FileStorageService(@Value("${app.upload.dir:}") String uploadDir) {
        // Si uploadDir está vacío, estamos en desarrollo
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            // Desarrollo: detectar directorio del proyecto automáticamente
            Path currentPath = Paths.get("").toAbsolutePath();
            
            // Si estamos en un workspace multi-proyecto, buscar visso-backend-main
            if (!currentPath.toString().contains("visso-backend")) {
                Path backendPath = currentPath.resolve("visso-backend-main");
                if (Files.exists(backendPath)) {
                    currentPath = backendPath;
                }
            }
            
            this.uploadLocation = currentPath.resolve("target/classes/static/images");
            
            // Sincronizar imágenes de src a target al iniciar
            Path srcImagesPath = currentPath.resolve("src/main/resources/static/images");
            if (Files.exists(srcImagesPath)) {
                syncImagesFromSrc(srcImagesPath, this.uploadLocation);
            }
        } else {
            // Producción: usar directorio externo configurado
            this.uploadLocation = Paths.get(uploadDir).toAbsolutePath();
        }
        
        try {
            Files.createDirectories(this.uploadLocation);
            System.out.println("Directorio de uploads: " + this.uploadLocation);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo crear el directorio de uploads", e);
        }
    }
    
    /**
     * Sincroniza las imágenes de src a target (solo las que no existen en target)
     */
    private void syncImagesFromSrc(Path srcPath, Path targetPath) {
        try {
            Files.walk(srcPath)
                .filter(Files::isRegularFile)
                .forEach(srcFile -> {
                    try {
                        Path relativePath = srcPath.relativize(srcFile);
                        Path targetFile = targetPath.resolve(relativePath);
                        
                        // Solo copiar si no existe en target
                        if (!Files.exists(targetFile)) {
                            Files.createDirectories(targetFile.getParent());
                            Files.copy(srcFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Sincronizado: " + relativePath);
                        }
                    } catch (IOException e) {
                        System.err.println("Error al sincronizar " + srcFile + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            System.err.println("Error al sincronizar imágenes: " + e.getMessage());
        }
    }

    /**
     * Guarda un archivo de imagen de producto en la carpeta correspondiente a su categoría
     * @param file Archivo a guardar
     * @param categoria Nombre de la categoría (ej: "OPTICOS", "SOL", "CONTACTO", "ACCESORIOS")
     * @return Ruta relativa de la imagen guardada (ej: "/images/PRODUCTOS/SOL/s_9.webp")
     */
    public String storeProductImage(MultipartFile file, String categoria) {
        if (file.isEmpty()) {
            throw new FileStorageException("No se puede subir un archivo vacío");
        }
        
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Validar path traversal
            if (originalName.contains("..")) {
                throw new FileStorageException("Nombre de archivo inválido: " + originalName);
            }
            
            // Validar extensión
            String extension = getFileExtension(originalName).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new FileStorageException(
                    "Extensión no permitida: " + extension + ". Solo se permiten: " + ALLOWED_EXTENSIONS
                );
            }
            
            // Crear ruta de destino: PRODUCTOS/{CATEGORIA}/
            Path categoryDir = uploadLocation.resolve("PRODUCTOS").resolve(categoria.toUpperCase());
            Files.createDirectories(categoryDir);
            
            // Generar nombre único manteniendo extensión original
            String fileName = generateUniqueFileName(originalName);
            Path targetLocation = categoryDir.resolve(fileName);
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // También guardar en src para persistencia
            Path srcPath = Paths.get("").toAbsolutePath();
            if (!srcPath.toString().contains("visso-backend")) {
                Path backendPath = srcPath.resolve("visso-backend-main");
                if (Files.exists(backendPath)) {
                    srcPath = backendPath;
                }
            }
            Path srcImagesDir = srcPath.resolve("src/main/resources/static/images/PRODUCTOS").resolve(categoria.toUpperCase());
            Files.createDirectories(srcImagesDir);
            Path srcLocation = srcImagesDir.resolve(fileName);
            Files.copy(file.getInputStream(), srcLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Retornar ruta relativa para guardar en BD
            return "/images/PRODUCTOS/" + categoria.toUpperCase() + "/" + fileName;
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo guardar el archivo " + originalName, ex);
        }
    }

    /**
     * Genera un nombre único basado en timestamp y nombre original
     */
    private String generateUniqueFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String nameWithoutExt = originalName.substring(0, originalName.lastIndexOf('.'));
        // Limpiar caracteres especiales del nombre
        nameWithoutExt = nameWithoutExt.replaceAll("[^a-zA-Z0-9-_]", "_");
        return nameWithoutExt + "_" + System.currentTimeMillis() + "." + extension;
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }

    /**
     * Carga un archivo como recurso (para servir imágenes)
     */
    public Resource loadFileAsResource(String imagePath) {
        try {
            // imagePath viene como "/images/PRODUCTOS/SOL/s_1.webp"
            // Remover el prefijo "/images/" para obtener la ruta relativa
            String relativePath = imagePath.startsWith("/images/") 
                ? imagePath.substring("/images/".length()) 
                : imagePath;
            
            Path filePath = uploadLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) return resource;
            else throw new FileStorageException("Archivo no encontrado: " + imagePath);
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Archivo no encontrado: " + imagePath, ex);
        }
    }

    /**
     * Elimina un archivo de imagen
     */
    public boolean deleteFile(String imagePath) {
        try {
            String relativePath = imagePath.startsWith("/images/") 
                ? imagePath.substring("/images/".length()) 
                : imagePath;
            
            Path filePath = uploadLocation.resolve(relativePath).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            return false;
        }
    }
}
