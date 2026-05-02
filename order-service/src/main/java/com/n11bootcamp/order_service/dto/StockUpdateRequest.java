package com.n11bootcamp.order_service.dto;

import java.util.List;

public class StockUpdateRequest {

    private List<StockItem> items;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(List<StockItem> items) {
        this.items = items;
    }

    public List<StockItem> getItems() { return items; }
    public void setItems(List<StockItem> items) { this.items = items; }

    public static class StockItem {
        private Long productId;
        private Integer quantity;

        public StockItem() {
        }

        public StockItem(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
