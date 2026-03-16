package com.techie.inventory_service.service;

public interface InventoryService {

    boolean isInStock(Long productId, Integer quantity);

    void reduceStock(Long productId, Integer quantity);
}