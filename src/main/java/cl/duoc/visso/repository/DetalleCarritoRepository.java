package cl.duoc.visso.repository;

import cl.duoc.visso.dto.ProductSalesDTO;
import cl.duoc.visso.model.Carrito;
import cl.duoc.visso.model.Cotizacion;
import cl.duoc.visso.model.DetalleCarrito;
import cl.duoc.visso.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {
    
    List<DetalleCarrito> findByCarrito(Carrito carrito);
    
    // Busca si ya existe este producto CON esa cotización específica
    Optional<DetalleCarrito> findByCarritoAndProductoAndCotizacion(Carrito carrito, Producto producto, Cotizacion cotizacion);

    // Busca si ya existe este producto SIN cotización (Lentes de sol, accesorios)
    Optional<DetalleCarrito> findByCarritoAndProductoAndCotizacionIsNull(Carrito carrito, Producto producto);


    // TOP productos vendidos (cantidad + total)
    @Query("""
        SELECT new cl.duoc.visso.dto.ProductSalesDTO(
            d.nombreProducto,
            SUM(d.cantidad),
            SUM(d.precioUnitario * d.cantidad)
        )
        FROM DetalleCarrito d
        WHERE d.carrito.estado = 'C'
        GROUP BY d.nombreProducto
        ORDER BY SUM(d.cantidad) DESC
    """)
    List<ProductSalesDTO> getTopSellingProducts();

    // Ventas por producto (para gráfico torta)
    @Query("""
        SELECT new cl.duoc.visso.dto.ProductSalesDTO(
            d.nombreProducto,
            SUM(d.cantidad),
            SUM(d.precioUnitario * d.cantidad)
        )
        FROM DetalleCarrito d
        WHERE d.carrito.estado = 'C'
        GROUP BY d.nombreProducto
    """)
    List<ProductSalesDTO> getSalesByProduct();
}