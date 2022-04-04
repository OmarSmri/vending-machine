package com.omar.vendingmachine.controller;

import com.omar.vendingmachine.exceptions.InvalidPurchaseException;
import com.omar.vendingmachine.exceptions.ProductDoesNotExistException;
import com.omar.vendingmachine.model.product.Product;
import com.omar.vendingmachine.pojo.ProductPojo;
import com.omar.vendingmachine.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductPojo productPojo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Product product;
        try {
            product = productService.createProduct(productPojo, username);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestBody ProductPojo productPojo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Product product;
        try {
            product = productService.updateProduct(productPojo, username);
        } catch (ProductDoesNotExistException | ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            productService.deleteProduct(id, username);
        } catch (ProductDoesNotExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(String.format("successfully deleted product %s", id));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        Product product = productService.findProductById(id);
        if (product == null) {
            return ResponseEntity.ok().body(String.format("No product with id %s was found", id));
        }
        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/product/buy/{id}")
    public ResponseEntity<?> buyProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Map<String, Object> result;
        try {
            result = productService.buyProduct(id, username);
        } catch (InvalidPurchaseException | ProductDoesNotExistException e) {
            return ResponseEntity.ok().body(e.getMessage());
        }
        return ResponseEntity.ok().body(result);
    }


}
