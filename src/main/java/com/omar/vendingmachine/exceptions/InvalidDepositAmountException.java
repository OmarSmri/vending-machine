package com.omar.vendingmachine.exceptions;

public class InvalidDepositAmountException extends Exception {
    public InvalidDepositAmountException(String msg) {
        super(msg);
    }
}
