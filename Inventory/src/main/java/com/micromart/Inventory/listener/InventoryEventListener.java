package com.micromart.Inventory.listener;

import com.micromart.Inventory.event.DeductStockEvent;
import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventListener.class);
    private final InventoryService inventoryService;

    public InventoryEventListener(InventoryService inventoryService){
        this.inventoryService=inventoryService;

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "inventory.deduct.queue", durable = "true"),
            exchange = @Exchange(value = "micromart.exchange", type = "topic"),
            key = "inventory.deduct"
    ))
    public void handleDeductStockEvent(DeductStockEvent event) {
        logger.info("Received stock deduction event from Order Service for Order: {}", event.getOrderNumber());

        try {
            for (DeductStockEvent.EventLineItem item : event.getItems()) {

                logger.info("Preparing to deduct {} units of SKU: {}", item.getQuantity(), item.getSkuCode());

                InventoryDto dto = new InventoryDto();
                dto.setSkuCode(item.getSkuCode());
                dto.setQuantity(item.getQuantity());

                inventoryService.deductStock(dto);

                logger.info("Successfully deducted stock for SKU: {}", item.getSkuCode());
            }

            logger.info("Finished processing all stock deductions for Order: {}", event.getOrderNumber());

        } catch (Exception e) {
            logger.error("Failed to process stock deduction for Order {}. Error: {}", event.getOrderNumber(), e.getMessage());
        }
    }
}
