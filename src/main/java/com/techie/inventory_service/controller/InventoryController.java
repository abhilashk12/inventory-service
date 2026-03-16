package com.techie.inventory_service.controller;

import com.techie.inventory_service.service.InventoryService;
import com.techie.inventory_service.service.InventoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.isInStock(productId, quantity)
        );
    }

    @PostMapping("/reduce")
    public ResponseEntity<String> reduceStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        inventoryService.reduceStock(productId, quantity);
        return ResponseEntity.ok("Stock updated");
    }
}
