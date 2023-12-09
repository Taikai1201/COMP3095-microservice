package ca.gbc.productservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Injects Lombok to the class
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
}
