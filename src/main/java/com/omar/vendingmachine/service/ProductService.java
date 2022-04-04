package com.omar.vendingmachine.service;

import com.omar.vendingmachine.constants.ProductConstants;
import com.omar.vendingmachine.exceptions.InvalidPurchaseException;
import com.omar.vendingmachine.exceptions.ProductDoesNotExistException;
import com.omar.vendingmachine.model.product.Product;
import com.omar.vendingmachine.pojo.ProductPojo;
import com.omar.vendingmachine.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomUserDetailService customUserDetailService;


    public Product createProduct(ProductPojo productPojo, String username) throws ConstraintViolationException {
        Product product = new Product();
        product.setProductName(productPojo.getProductName());
        product.setSellerUserName(username);
        product.setAmountAvaillable(productPojo.getAmountAvaillable());
        product.setCost(productPojo.getCost());
        return saveProduct(product);
    }

    public Product updateProduct(ProductPojo productPojo, String username) throws ProductDoesNotExistException, ConstraintViolationException {
        Product updatedProduct = findProductById(productPojo.getId());
        if (updatedProduct == null || !updatedProduct.getSellerUserName().equalsIgnoreCase(username)) {
            throw new ProductDoesNotExistException(String.format("No product with id %s exist for seller %s",
                    productPojo.getId(), username));
        }
        if (StringUtils.isNoneBlank(productPojo.getProductName())) {
            updatedProduct.setProductName(productPojo.getProductName());
        }
        if (productPojo.getAmountAvaillable() != null) {
            updatedProduct.setAmountAvaillable(productPojo.getAmountAvaillable());
        }
        if (productPojo.getCost() != null) {
            updatedProduct.setCost(productPojo.getCost());
        }
        return saveProduct(updatedProduct);
    }

    public void deleteProduct(String id, String username) throws ProductDoesNotExistException {
        Product deletedProduct = findProductById(id);
        if (deletedProduct == null || !deletedProduct.getSellerUserName().equalsIgnoreCase(username)) {
            throw new ProductDoesNotExistException(String.format("No product with id %s exist for seller %s",
                    id, username));
        }
        deletedProduct.setDeleted(true);
        saveProduct(deletedProduct);
    }

    public Map<String, Object> buyProduct(String id, String username) throws ProductDoesNotExistException, InvalidPurchaseException {
        Map<String, Object> result = new HashMap<>();
        Product product = findProductById(id);
        if (product == null || product.isDeleted()) {
            throw new ProductDoesNotExistException(String.format("No Product %s availlable to Purchase", id));
        }
        if (product.getAmountAvaillable() == 0) {
            throw new InvalidPurchaseException(String.format("Product %s is out of stock", id));
        }
        List<Integer> change = customUserDetailService.completePayment(username, product.getCost());
        product.setAmountAvaillable(product.getAmountAvaillable() - 1);
        product = saveProduct(product);
        result.put(ProductConstants.PRODUCT, product);
        result.put(ProductConstants.CHANGE, change);
        return result;
    }

    public Product findProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) throws ConstraintViolationException {
        return productRepository.save(product);
    }
}
