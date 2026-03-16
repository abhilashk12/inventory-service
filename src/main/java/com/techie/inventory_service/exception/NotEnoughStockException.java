package com.techie.inventory_service.exception;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException() {
        super("Not enough stock");
    }
}