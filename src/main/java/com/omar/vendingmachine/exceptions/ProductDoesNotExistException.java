package com.omar.vendingmachine.exceptions;

/**
 * This is to be thrown in any case of attempt to retrieve a product that does not exist.
 */
public class ProductDoesNotExistException extends Exception {
    public ProductDoesNotExistException(String msg) {
        super(msg);
    }
}
