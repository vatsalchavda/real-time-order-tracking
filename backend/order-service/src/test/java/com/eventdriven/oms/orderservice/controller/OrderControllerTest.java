package com.eventdriven.oms.orderservice.controller;

import com.eventdriven.oms.common.dto.OrderDTO;
import com.eventdriven.oms.common.dto.OrderItemDTO;
import com.eventdriven.oms.common.enums.OrderStatus;
import com.eventdriven.oms.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    public void createOrder_shouldReturnAccepted_andIncludeRequestIdHeader() throws Exception {
        // Arrange - build a sample request
        OrderItemDTO item = OrderItemDTO.builder()
                .productId("PROD1")
                .productName("Test Product")
                .quantity(1)
                .price(9.99)
                .build();

        OrderDTO request = OrderDTO.builder()
                .customerId("CUST1")
                .customerName("Alice")
                .items(List.of(item))
                .totalAmount(9.99)
                .shippingAddress("123 Main St")
                .build();

        OrderDTO response = OrderDTO.builder()
                .orderId("order-123")
                .customerId("CUST1")
                .customerName("Alice")
                .items(List.of(item))
                .totalAmount(9.99)
                .status(OrderStatus.PENDING)
                .build();

        Mockito.when(orderService.createOrder(any(OrderDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(header().string("X-Request-Id", notNullValue()));
    }
}
