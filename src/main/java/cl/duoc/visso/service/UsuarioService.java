package cl.duoc.visso.service;

import cl.duoc.visso.model.Usuario;
import cl.duoc.visso.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> actualizarUsuario(Long id, Usuario usuarioEditado) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Actualizamos los datos permitidos
            usuarioExistente.setNombre(usuarioEditado.getNombre());
            usuarioExistente.setApellido(usuarioEditado.getApellido());
            usuarioExistente.setEmail(usuarioEditado.getEmail());
            // Permitir actualizar el rol si viene en la petición
            if (usuarioEditado.getRol() != null) {
                usuarioExistente.setRol(usuarioEditado.getRol());
            }
            return usuarioRepository.save(usuarioExistente);
        });
    }

    // --- MÉTODOS NUEVOS PARA EL ADMIN ---

    // 1. Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // 2. Eliminar un usuario por ID
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // 3. Crear un nuevo usuario (ADMIN)
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Formatear RUT antes de guardar
        String rutFormateado = formatearRut(usuario.getRut());
        if (usuarioRepository.existsByRut(rutFormateado)) {
            throw new RuntimeException("El RUT ya está registrado");
        }
        usuario.setRut(rutFormateado);
        
        // Encriptar contraseña
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isEmpty()) {
            usuario.setPasswordHash(encoder.encode(usuario.getPasswordHash()));
        }
        
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        if (usuario.getRol() == null) usuario.setRol("USER");

        return usuarioRepository.save(usuario);
    }
    
    /**
     * Formatea un RUT al formato estándar: XX.XXX.XXX-X
     */
    private String formatearRut(String rut) {
        if (rut == null || rut.isEmpty()) {
            return rut;
        }
        
        // Limpiar el RUT (quitar puntos y guión)
        String rutLimpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();
        
        if (rutLimpio.length() < 2) {
            return rut;
        }
        
        // Separar número y dígito verificador
        String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
        String dv = rutLimpio.substring(rutLimpio.length() - 1);
        
        // Formatear con puntos
        StringBuilder rutFormateado = new StringBuilder();
        int contador = 0;
        
        for (int i = numero.length() - 1; i >= 0; i--) {
            if (contador == 3) {
                rutFormateado.insert(0, ".");
                contador = 0;
            }
            rutFormateado.insert(0, numero.charAt(i));
            contador++;
        }
        
        rutFormateado.append("-").append(dv);
        return rutFormateado.toString();
    }
}