package cl.duoc.visso.controller;

import cl.duoc.visso.dto.ProductSalesDTO;
import cl.duoc.visso.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<ProductSalesDTO>> getTopProducts() {
        return ResponseEntity.ok(analyticsService.getTopSellingProducts());
    }

    @GetMapping("/sales-by-product")
    public ResponseEntity<List<ProductSalesDTO>> getSalesByProduct() {
        return ResponseEntity.ok(analyticsService.getSalesByProduct());
    }

    @GetMapping("/total-sales")
    public ResponseEntity<BigDecimal> getTotalSales() {
        return ResponseEntity.ok(analyticsService.getTotalSales());
    }
}
