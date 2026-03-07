The Inventory Service manages product stock levels.

Key Responsibilities:
- Listen to OrderCreatedEvent from Kafka
- Reserve product inventory
- Publish InventoryReservedEvent or InventoryFailedEvent
- Maintain product stock consistency

Tech Stack:
- Spring Boot
- Apache Kafka
- MySQL
- JPA / Hibernate
- Docker
