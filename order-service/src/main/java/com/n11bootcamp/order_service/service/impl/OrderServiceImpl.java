package com.n11bootcamp.order_service.service.impl;

import com.n11bootcamp.order_service.dto.CreateOrderRequest;
import com.n11bootcamp.order_service.dto.OrderResponse;
import com.n11bootcamp.order_service.dto.StockReserveRequestedEvent;
import com.n11bootcamp.order_service.entity.Order;
import com.n11bootcamp.order_service.entity.OrderDetails;
import com.n11bootcamp.order_service.entity.OrderItem;
import com.n11bootcamp.order_service.entity.OrderStatus;
import com.n11bootcamp.order_service.repository.OrderRepository;
import com.n11bootcamp.order_service.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${stock.rabbit.exchange}")
    private String stockExchange;

    @Value("${stock.rabbit.reserveRequestedRoutingKey}")
    private String stockReserveRequestedRoutingKey;

    public OrderServiceImpl(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUsername(request.getUsername());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(itemRequest.getProductId());
                    item.setProductName(itemRequest.getProductName());
                    item.setPrice(itemRequest.getPrice());
                    item.setQuantity(itemRequest.getQuantity());
                    return item;
                })
                .toList();
        order.setItems(items);
        order.setTotalPrice(calculateTotal(items));
        order.setOrderDetails(toOrderDetails(request));

        Order savedOrder = orderRepository.save(order);
        publishStockReserveRequest(savedOrder);
        return toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    @Override
    public List<OrderResponse> findOrdersByUsername(String username) {
        return orderRepository.findByUsername(username).stream()
                .map(this::toResponse)
                .toList();
    }

    private void publishStockReserveRequest(Order order) {
        List<StockReserveRequestedEvent.Item> eventItems = order.getItems().stream()
                .map(item -> new StockReserveRequestedEvent.Item(item.getProductId(), item.getQuantity()))
                .toList();

        StockReserveRequestedEvent event = new StockReserveRequestedEvent(
                order.getId(),
                order.getUsername(),
                eventItems
        );

        rabbitTemplate.convertAndSend(stockExchange, stockReserveRequestedRoutingKey, event);
    }

    private OrderDetails toOrderDetails(CreateOrderRequest request) {
        OrderDetails details = new OrderDetails();
        details.setFirstName(request.getFirstName());
        details.setLastName(request.getLastName());
        details.setStreetAddress(request.getStreetAddress());
        details.setCity(request.getCity());
        details.setCountry(request.getCountry());
        details.setPhone(request.getPhone());
        details.setEmail(request.getEmail());
        return details;
    }

    private Double calculateTotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> safePrice(item.getPrice()) * safeQuantity(item.getQuantity()))
                .sum();
    }

    private double safePrice(Double price) {
        return price == null ? 0 : price;
    }

    private int safeQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setUsername(order.getUsername());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());

        List<OrderResponse.OrderItemResponse> responseItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setProductId(item.getProductId());
            itemResponse.setProductName(item.getProductName());
            itemResponse.setPrice(item.getPrice());
            itemResponse.setQuantity(item.getQuantity());
            responseItems.add(itemResponse);
        }
        response.setItems(responseItems);
        return response;
    }
}
