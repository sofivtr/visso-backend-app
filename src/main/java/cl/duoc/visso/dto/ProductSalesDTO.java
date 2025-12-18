package cl.duoc.visso.dto;

import java.math.BigDecimal;

public class ProductSalesDTO {

    private String productName;
    private Long quantity;
    private BigDecimal totalSales;

    public ProductSalesDTO(String productName, Long quantity, BigDecimal totalSales) {
        this.productName = productName;
        this.quantity = quantity;
        this.totalSales = totalSales;
    }

    public String getProductName() {
        return productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }
}
