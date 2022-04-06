package com.omar.vendingmachine.exceptions;

/**
 * This is to be thrown in case of invalid purchase operation, these could be attempts to purchase an out-of-stock product
 * or buying a product wih no suffecient funds at the buying user account.
 */
public class InvalidPurchaseException extends Exception {
    public InvalidPurchaseException(String msg) {
        super(msg);
    }
}
