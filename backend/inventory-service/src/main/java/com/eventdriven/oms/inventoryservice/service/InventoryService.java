package com.eventdriven.oms.inventoryservice.service;

import com.eventdriven.oms.common.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

    public void processInventoryCheck(OrderDTO order) {
        // Simple placeholder: in a real service this would check DB/stock and reserve
        log.info("Processing inventory check for order {}", order.getOrderId());
    }

    public void releaseInventory(OrderDTO order) {
        // Placeholder for compensation logic to release reserved inventory
        log.info("Releasing inventory for order {}", order.getOrderId());
    }
}
