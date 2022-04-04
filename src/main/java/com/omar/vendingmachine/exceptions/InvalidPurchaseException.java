package com.omar.vendingmachine.exceptions;

public class InvalidPurchaseException extends Exception{
    public InvalidPurchaseException(String msg){
        super(msg);
    }
}
