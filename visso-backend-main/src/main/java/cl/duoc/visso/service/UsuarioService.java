package cl.duoc.visso.service;

import cl.duoc.visso.model.Usuario;
import cl.duoc.visso.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> actualizarUsuario(Long id, Usuario usuarioEditado) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Actualizamos solo los datos permitidos
            usuarioExistente.setNombre(usuarioEditado.getNombre());
            usuarioExistente.setApellido(usuarioEditado.getApellido());
            usuarioExistente.setEmail(usuarioEditado.getEmail());
            // No tocamos Password, Rol ni Rut por seguridad en este endpoint
            
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
}