package ca.gbc.productservices.service;

import ca.gbc.productservices.dto.ProductRequest;
import ca.gbc.productservices.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequest productRequest);
    String updateProduct(String productId, ProductRequest productRequest);

    void deleteProduct(String productId);

    List<ProductResponse> getAllProducts();


}
