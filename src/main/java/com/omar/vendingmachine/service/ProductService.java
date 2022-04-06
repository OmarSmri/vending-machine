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


    /**
     * Lists all the products in the products collection in the database --> for testing purposes only.
     * @return
     */
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    /**
     * Hard deletes all the products in the products collecion in the database --> for testing purposes only.
     */
    public void deleteAll() {
        productRepository.deleteAll();
    }

    /**
     * Creates product and insert it to the database using the parameters from the ProductPojo object, it gives it to the seller
     * with the input username
     * @param productPojo
     * @param username
     * @return
     * @throws ConstraintViolationException In case any value violates the constraints.
     */
    public Product createProduct(ProductPojo productPojo, String username) throws ConstraintViolationException {
        Product product = new Product();
        product.setProductName(productPojo.getProductName());
        product.setSellerUserName(username);
        product.setAmountAvaillable(productPojo.getAmountAvaillable());
        if (productPojo.getCost() % 5 != 0) {
            throw new ConstraintViolationException("The cost should be multiple of 5", null);
        }
        product.setCost(productPojo.getCost());
        return saveProduct(product);
    }

    /**
     * Updates a product in the database to only the values that are not null in the ProductPojo object. It uses username to verify
     * that the user performing the update is the owner of the product to be updated.
     * @param productPojo
     * @param username
     * @return
     * @throws ProductDoesNotExistException In case the product does not exist or no product with the input id belongs to the input user.
     * @throws ConstraintViolationException In case any value violates the constraints.
     */
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
            if (productPojo.getCost() % 5 != 0) {
                throw new ConstraintViolationException("The cost should be multiple of 5", null);
            }
            updatedProduct.setCost(productPojo.getCost());
        }
        return saveProduct(updatedProduct);
    }

    /**
     * Soft deletes the product with the input id. It uses the username to verify that the user who is performing the delete
     * is the owner of the product to be deleted.
     * @param id
     * @param username
     * @throws ProductDoesNotExistException In case the product does not exist or no product with the input id belongs to the input user.
     */
    public void deleteProduct(String id, String username) throws ProductDoesNotExistException {
        Product deletedProduct = findProductById(id);
        if (deletedProduct == null || !deletedProduct.getSellerUserName().equalsIgnoreCase(username)) {
            throw new ProductDoesNotExistException(String.format("No product with id %s exist for seller %s",
                    id, username));
        }
        deletedProduct.setDeleted(true);
        saveProduct(deletedProduct);
    }

    /**
     * Performs product purchase operation buy the user with the input username. The operation includes verification that the purchase
     * can be operated (enought deposit and product is in stock and not deleted). It also includes updating the deposit value for the user
     * and the amount availlable value for the product.
     * @param id
     * @param username
     * @return
     * @throws ProductDoesNotExistException
     * @throws InvalidPurchaseException
     */
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

    /**
     * Finds a product with the input id.
     * @param id
     * @return
     */
    public Product findProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Saves product to the database. It performs insertion in case the product does not exist in the database and update
     * in case the product originally exists in the database.
     * @param product
     * @return
     * @throws ConstraintViolationException
     */
    public Product saveProduct(Product product) throws ConstraintViolationException {
        return productRepository.save(product);
    }
}
