package com.techie.inventory_service.consumer;

import com.techie.inventory_service.event.InventoryReservedEvent;
import com.techie.inventory_service.event.InventoryUpdatedEvent;
import com.techie.inventory_service.event.OrderCreatedEvent;
import com.techie.inventory_service.event.ProcessedEvent;
import com.techie.inventory_service.producer.InventoryProducer;
import com.techie.inventory_service.repository.ProcessedEventRepository;
import com.techie.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final InventoryService inventoryService;
    private final ProcessedEventRepository processedEventRepository;
    private final InventoryProducer inventoryProducer;

    @KafkaListener(topics = "order-created-topic", groupId = "inventory-group")
    public void consume(OrderCreatedEvent event) {

        if (event.getOrderId() == null) {
            System.out.println("Invalid event received: orderId is null");
            return;
        }

        if (processedEventRepository.existsById(event.getOrderId())) {
            System.out.println("Duplicate event ignored");
            return;
        }

        try {

            inventoryService.reduceStock(event.getProductId(),event.getQuantity());

            ProcessedEvent p = processedEventRepository.save(new ProcessedEvent(event.getOrderId()));

            InventoryUpdatedEvent inventoryEvent =
                    new InventoryUpdatedEvent(
                            event.getOrderId(),
                            "CONFIRMED"
                    );

            inventoryProducer.publishInventoryEvent(inventoryEvent);

        } catch (Exception e) {

            InventoryUpdatedEvent inventoryEvent =
                    new InventoryUpdatedEvent(
                            event.getOrderId(),
                            "FAILED"
                    );

            inventoryProducer.publishInventoryEvent(inventoryEvent);
        }
    }
}
