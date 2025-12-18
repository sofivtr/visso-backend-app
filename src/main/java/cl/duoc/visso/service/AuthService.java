package cl.duoc.visso.service;

import cl.duoc.visso.model.Usuario;
import cl.duoc.visso.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Formatear RUT antes de guardar
        String rutFormateado = formatearRut(usuario.getRut());
        if (usuarioRepository.existsByRut(rutFormateado)) {
            throw new RuntimeException("El RUT ya está registrado");
        }
        usuario.setRut(rutFormateado);
        
        usuario.setPasswordHash(encoder.encode(usuario.getPasswordHash()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        if (usuario.getRol() == null) usuario.setRol("USER");

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (encoder.matches(password, usuario.getPasswordHash())) {
                return usuario;
            }
        }
        return null;
    }

    public boolean existeCorreo(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean recuperarContrasena(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setPasswordHash(encoder.encode("visso1234"));
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
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