package cl.duoc.visso.controller;

import cl.duoc.visso.dto.SolicitudCarrito;
import cl.duoc.visso.model.Carrito;
import cl.duoc.visso.repository.DetalleCarritoRepository;
import cl.duoc.visso.service.CarritoService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;
    private final DetalleCarritoRepository detalleCarritoRepository;

    public CarritoController(CarritoService carritoService, DetalleCarritoRepository detalleCarritoRepository) {
        this.carritoService = carritoService;
        this.detalleCarritoRepository = detalleCarritoRepository;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(usuarioId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody SolicitudCarrito solicitud) {
        carritoService.agregarProducto(
            solicitud.getUsuarioId(), 
            solicitud.getProductoId(), 
            solicitud.getCantidad(),
            solicitud.getCotizacionId()
        );
        return ResponseEntity.ok("Producto agregado al carrito correctamente");
    }

    @PostMapping("/cerrar/{usuarioId}")
    public ResponseEntity<?> cerrarCarrito(@PathVariable Long usuarioId) {
        carritoService.cerrarCarrito(usuarioId);
        return ResponseEntity.ok("Compra realizada con éxito.");
    }


    @GetMapping("/ventas")
    public ResponseEntity<List<Carrito>> obtenerVentas() {
        // Aquí podrías validar que el usuario sea ADMIN si usaras tokens,
        // pero por ahora está bien así.
        return ResponseEntity.ok(carritoService.listarVentas());
    }

    @DeleteMapping("/detalle/{detalleId}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Long detalleId) {
        try {
            detalleCarritoRepository.deleteById(detalleId);
            return ResponseEntity.ok("Producto eliminado del carrito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar: " + e.getMessage());
        }
}
}