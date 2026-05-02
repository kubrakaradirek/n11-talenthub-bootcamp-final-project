package com.n11bootcamp.order_service.service.impl;

import com.n11bootcamp.order_service.dto.CreateOrderRequest;
import com.n11bootcamp.order_service.dto.OrderResponse;
import com.n11bootcamp.order_service.dto.StockReserveRequestedEvent;
import com.n11bootcamp.order_service.entity.Order;
import com.n11bootcamp.order_service.entity.OrderDetails;
import com.n11bootcamp.order_service.entity.OrderItem;
import com.n11bootcamp.order_service.entity.OrderStatus;
import com.n11bootcamp.order_service.repository.OrderRepository;
import com.n11bootcamp.order_service.service.CouponService;
import com.n11bootcamp.order_service.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class); // Bu alan sayesinde log.info ve log.error kullanabiliyoruz.

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CouponService couponService;

    @Value("${stock.rabbit.exchange}")
    private String stockExchange;

    @Value("${stock.rabbit.reserveRequestedRoutingKey}")
    private String stockReserveRequestedRoutingKey;

    public OrderServiceImpl(OrderRepository orderRepository, RabbitTemplate rabbitTemplate, CouponService couponService) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.couponService = couponService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Sipariş alınıyor..."); // Metot başladığında sipariş akışının başladığını loglara yazıyoruz.

        try { // Sipariş oluştururken hata çıkabilecek işlemleri bu blok içinde topluyoruz.
            if (request.getItems() == null || request.getItems().isEmpty()) { // Ürün listesi boş mu diye kontrol ediyoruz.
                throw new IllegalArgumentException("Sipariş en az bir ürün içermelidir"); // İş kuralı hatası varsa anlaşılır mesaj fırlatıyoruz.
            }

            Order order = new Order(); // Veritabanına kaydedilecek yeni sipariş nesnesini oluşturuyoruz.
            order.setUsername(request.getUsername()); // Siparişin hangi kullanıcıya ait olduğunu set ediyoruz.
            order.setUserId(request.getUserId()); // Kupon üretirken gerçek kullanıcı id bilgisini kullanmak için set ediyoruz.
            order.setStatus(OrderStatus.CREATED); // Siparişi ilk olarak CREATED durumunda başlatıyoruz.

            List<OrderItem> items = request.getItems().stream() // İstekten gelen ürün listesini sipariş ürünlerine çeviriyoruz.
                    .map(itemRequest -> { // Her bir ürün isteğini OrderItem nesnesine dönüştürüyoruz.
                        OrderItem item = new OrderItem(); // Sipariş içinde tutulacak ürün nesnesini oluşturuyoruz.
                        item.setProductId(itemRequest.getProductId()); // Ürün id bilgisini sipariş ürününe koyuyoruz.
                        item.setProductName(itemRequest.getProductName()); // Ürün adını sipariş ürününe koyuyoruz.
                        item.setPrice(itemRequest.getPrice()); // Ürün fiyatını sipariş ürününe koyuyoruz.
                        item.setQuantity(itemRequest.getQuantity()); // Ürün adetini sipariş ürününe koyuyoruz.
                        return item; // Hazırlanan ürünü listeye geri veriyoruz.
                    })
                    .toList(); // Dönüşen ürünleri liste haline getiriyoruz.
            order.setItems(items); // Hazırladığımız ürünleri siparişe bağlıyoruz.
            Double totalPrice = calculateTotal(items); // Siparişin indirimsiz toplam fiyatını hesaplıyoruz.
            Double finalPrice = couponService.applyCouponIfPresent(request.getUserId(), request.getCouponCode(), totalPrice); // Kupon varsa yüzde 20 indirimi uyguluyoruz.
            order.setTotalPrice(finalPrice); // Siparişin ödenecek son toplamını set ediyoruz.
            order.setOrderDetails(toOrderDetails(request)); // Adres ve iletişim bilgilerini siparişe ekliyoruz.

            Order savedOrder = orderRepository.save(order); // Siparişi veritabanına kaydediyoruz.
            publishStockReserveRequest(savedOrder); // Stok rezervasyonu için RabbitMQ mesajı gönderiyoruz.
            return toResponse(savedOrder); // Kaydedilen siparişi kullanıcıya cevap olarak dönüyoruz.
        } catch (RuntimeException exception) { // Sipariş sırasında oluşan iş hatalarını yakalıyoruz.
            log.error("Sipariş sırasında hata oluştu", exception); // Hatayı detaylarıyla loglara yazıyoruz.
            throw exception; // GlobalExceptionHandler yakalasın diye hatayı tekrar fırlatıyoruz.
        }
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
