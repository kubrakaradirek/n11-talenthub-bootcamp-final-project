package com.n11bootcamp.order_service.saga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.n11bootcamp.order_service.dto.StockUpdateRequest;
import com.n11bootcamp.order_service.dto.StockUpdateResponse;
import com.n11bootcamp.order_service.entity.Order;
import com.n11bootcamp.order_service.entity.OrderItem;
import com.n11bootcamp.order_service.entity.OrderStatus;
import com.n11bootcamp.order_service.repository.OrderRepository;
import com.n11bootcamp.order_service.service.CouponService;
import com.n11bootcamp.order_service.service.ShoppingCartClient;
import com.n11bootcamp.order_service.service.StockServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderSagaListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaListener.class);

    private final OrderRepository orderRepository;
    private final StockServiceClient stockServiceClient;
    private final ShoppingCartClient shoppingCartClient;
    private final CouponService couponService;

    public OrderSagaListener(
            OrderRepository orderRepository,
            StockServiceClient stockServiceClient,
            ShoppingCartClient shoppingCartClient,
            CouponService couponService) {
        this.orderRepository = orderRepository;
        this.stockServiceClient = stockServiceClient;
        this.shoppingCartClient = shoppingCartClient;
        this.couponService = couponService;
    }

    @Transactional
    @RabbitListener(queues = "${order.rabbit.stockReservedQueue}")
    public void onStockReserved(StockReservedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + event.getOrderId()));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED) {
            return;
        }

        order.setStatus(OrderStatus.STOCK_RESERVED);
        orderRepository.save(order);

        StockUpdateResponse commitResponse = stockServiceClient.commit(toStockUpdateRequest(order));
        if (commitResponse == null || !commitResponse.isSuccess()) {
            String message = commitResponse == null ? "Empty stock commit response" : commitResponse.getMessage();
            throw new IllegalStateException("Stock commit failed for order " + order.getId() + ": " + message);
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        couponService.createCouponForCompletedOrder(order.getUserId(), order.getTotalPrice());
        clearCartAfterSuccessfulOrder(order);
    }

    @Transactional
    @RabbitListener(queues = "${order.rabbit.stockRejectedQueue}")
    public void onStockRejected(StockRejectedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + event.getOrderId()));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private StockUpdateRequest toStockUpdateRequest(Order order) {
        List<StockUpdateRequest.StockItem> items = order.getItems().stream()
                .map(this::toStockItem)
                .toList();
        return new StockUpdateRequest(items);
    }

    private StockUpdateRequest.StockItem toStockItem(OrderItem item) {
        return new StockUpdateRequest.StockItem(item.getProductId(), item.getQuantity());
    }

    private void clearCartAfterSuccessfulOrder(Order order) {
        String username = order.getUsername();
        if (username == null || username.isBlank()) {
            log.warn("Order {} completed but cart was not cleared because username is empty.", order.getId());
            return;
        }

        try {
            shoppingCartClient.clearCart(username);
            log.info("Cart cleared for user {} after order {} completed.", username, order.getId());
        } catch (Exception exception) {
            log.error("Order {} completed but cart could not be cleared for user {}.", order.getId(), username, exception);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockReservedEvent {
        private Long orderId;
        private String username;
        private String message;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockRejectedEvent {
        private Long orderId;
        private String username;
        private String reason;
        private String message;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
