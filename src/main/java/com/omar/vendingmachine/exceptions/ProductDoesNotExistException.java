package com.omar.vendingmachine.exceptions;

public class ProductDoesNotExistException extends Exception {
    public ProductDoesNotExistException(String msg) {
        super(msg);
    }
}
