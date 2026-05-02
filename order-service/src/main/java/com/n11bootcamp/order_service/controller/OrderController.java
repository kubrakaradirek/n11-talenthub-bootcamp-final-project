package com.n11bootcamp.order_service.controller;

import com.n11bootcamp.order_service.dto.CreateOrderRequest;
import com.n11bootcamp.order_service.dto.CouponPreviewResponse;
import com.n11bootcamp.order_service.dto.OrderResponse;
import com.n11bootcamp.order_service.entity.Coupon;
import com.n11bootcamp.order_service.service.CouponService;
import com.n11bootcamp.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(
        origins = {"http://localhost:8763", "http://localhost:5173", "http://localhost:3000"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
@Tag(name = "Orders", description = "Authenticated order operations")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final CouponService couponService;

    public OrderController(OrderService orderService, CouponService couponService) {
        this.orderService = orderService;
        this.couponService = couponService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Creates an order for an authenticated checkout flow.")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    @Operation(summary = "List orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public OrderResponse getOrderById(@Parameter(required = true) @PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "List orders by username")
    public List<OrderResponse> getOrdersByUsername(@Parameter(required = true) @PathVariable String username) {
        return orderService.findOrdersByUsername(username);
    }

    @GetMapping("/coupons/preview")
    @Operation(
            summary = "Preview coupon",
            description = "Checks whether the coupon belongs to the user, verifies it is unused, and calculates the 20 percent discount before payment."
    )
    public CouponPreviewResponse previewCoupon(
            @Parameter(required = true) @RequestParam Long userId,
            @Parameter(required = true) @RequestParam String couponCode,
            @Parameter(required = true) @RequestParam Double totalPrice) {
        return couponService.previewCoupon(userId, couponCode, totalPrice);
    }

    @GetMapping("/coupons/user/{userId}")
    @Operation(
            summary = "List unused coupons",
            description = "Lists active coupons that were earned once after a completed order over 10.000 TL and have not been used yet."
    )
    public List<Coupon> getUnusedCoupons(@Parameter(required = true) @PathVariable Long userId) {
        return couponService.findUnusedCoupons(userId);
    }
}
