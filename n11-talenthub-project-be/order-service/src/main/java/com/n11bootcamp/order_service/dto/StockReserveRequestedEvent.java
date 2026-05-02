package com.n11bootcamp.order_service.dto;

import java.util.List;

public class StockReserveRequestedEvent {

    private Long orderId;
    private String username;
    private List<Item> items;

    public StockReserveRequestedEvent() {
    }

    public StockReserveRequestedEvent(Long orderId, String username, List<Item> items) {
        this.orderId = orderId;
        this.username = username;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {
        private Long productId;
        private Integer quantity;

        public Item() {
        }

        public Item(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
