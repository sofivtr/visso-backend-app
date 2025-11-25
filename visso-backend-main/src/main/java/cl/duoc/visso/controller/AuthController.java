package cl.duoc.visso.controller;

import cl.duoc.visso.model.Usuario;
import cl.duoc.visso.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(authService.registrar(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        Usuario usuario = authService.login(
                credenciales.get("email"), 
                credenciales.get("password")
        );
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }


    @PostMapping("/recuperar-password")
        public ResponseEntity<?> recuperarPassword(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe ingresar un correo.");
            }

            // Buscamos al usuario real
            // OJO: Tienes que agregar 'buscarPorEmail' en AuthService o usar el repo aquí.
            // Como ya tienes 'usuarioRepository' inyectado en AuthService, agreguemos un método allá.
            
            boolean resultado = authService.recuperarContrasena(email); // <--- Método nuevo que haremos abajo

            if (resultado) {
                return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", "Clave restablecida. Tu nueva contraseña temporal es: visso1234"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El correo no se encuentra registrado.");
            }
        }
}