package cl.duoc.visso.service;

import cl.duoc.visso.dto.ProductSalesDTO;
import cl.duoc.visso.repository.CarritoRepository;
import cl.duoc.visso.repository.DetalleCarritoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnalyticsService {

    private final DetalleCarritoRepository detalleRepository;
    private final CarritoRepository carritoRepository;

    public AnalyticsService(
            DetalleCarritoRepository detalleRepository,
            CarritoRepository carritoRepository
    ) {
        this.detalleRepository = detalleRepository;
        this.carritoRepository = carritoRepository;
    }

    public List<ProductSalesDTO> getTopSellingProducts() {
        return detalleRepository.getTopSellingProducts();
    }

    public List<ProductSalesDTO> getSalesByProduct() {
        return detalleRepository.getSalesByProduct();
    }

    public BigDecimal getTotalSales() {
        return carritoRepository.findByEstado("C")
                .stream()
                .map(c -> c.getTotal() != null ? c.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
