package com.omar.vendingmachine.exceptions;

/**
 * This is thrown in case the amount to be deposited to a user account is not within the allowed values.
 */
public class InvalidDepositAmountException extends Exception {
    public InvalidDepositAmountException(String msg) {
        super(msg);
    }
}
