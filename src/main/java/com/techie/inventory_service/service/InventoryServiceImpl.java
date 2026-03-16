package com.techie.inventory_service.service;

import com.techie.inventory_service.entity.Inventory;
import com.techie.inventory_service.exception.InventoryNotFoundException;
import com.techie.inventory_service.exception.NotEnoughStockException;
import com.techie.inventory_service.repository.InventoryRepository;
import com.techie.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl
        implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public boolean isInStock(Long productId,
                             Integer quantity) {

        log.info("Checking stock for productId: {}",
                productId);

        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(() ->
                                new InventoryNotFoundException(productId));

        return inventory.getQuantity() >= quantity;
    }

    @Override
    public void reduceStock(Long productId,
                            Integer quantity) {

        log.info("Reducing stock for productId: {}",
                productId);

        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(() ->
                                new InventoryNotFoundException(productId));

        if (inventory.getQuantity() < quantity) {
            throw new NotEnoughStockException();
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);

        inventoryRepository.save(inventory);

        log.info("Stock updated for productId: {}",
                productId);
    }
}