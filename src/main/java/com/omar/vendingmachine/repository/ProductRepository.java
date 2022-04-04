package com.omar.vendingmachine.repository;

import com.omar.vendingmachine.model.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
