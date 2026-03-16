package com.techie.inventory_service.consumer;

import com.techie.inventory_service.event.InventoryUpdatedEvent;
import com.techie.inventory_service.event.OrderCreatedEvent;
import com.techie.inventory_service.event.ProcessedEvent;
import com.techie.inventory_service.producer.InventoryProducer;
import com.techie.inventory_service.repository.ProcessedEventRepository;
import com.techie.inventory_service.service.InventoryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final InventoryServiceImpl inventoryServiceImpl;
    private final ProcessedEventRepository processedEventRepository;
    private final InventoryProducer inventoryProducer;

    @KafkaListener(topics = "order-created-topic", groupId = "inventory-group")
    public void consume(OrderCreatedEvent event) {

        log.info("Received order event {}", event.getOrderId());

        if (event.getOrderId() == null) {
            System.out.println("Invalid event received: orderId is null");
            return;
        }

        if (processedEventRepository.existsById(event.getOrderId())) {
            System.out.println("Duplicate event ignored");
            return;
        }

        try {

            inventoryServiceImpl.reduceStock(event.getProductId(),event.getQuantity());

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
