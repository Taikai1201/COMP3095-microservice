package ca.gbc.productservices.repository;

import ca.gbc.productservices.model.Product;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    @DeleteQuery
    void deleteById(String product_id);

}
