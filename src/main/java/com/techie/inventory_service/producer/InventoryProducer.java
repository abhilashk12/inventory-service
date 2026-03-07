package com.techie.inventory_service.producer;

import com.techie.inventory_service.event.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, InventoryUpdatedEvent> kafkaTemplate;

    public void publishInventoryEvent(InventoryUpdatedEvent event) {

        kafkaTemplate.send("inventory-updated-topic", event);

        System.out.println("Inventory result published");
    }
}
