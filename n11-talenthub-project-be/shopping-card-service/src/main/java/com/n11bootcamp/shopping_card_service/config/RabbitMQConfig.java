package com.n11bootcamp.shopping_card_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CART_QUEUE = "shopping_cart_queue";

    @Bean
    public Queue cartQueue() {
        // true parametresi kuyruğun kalıcı (durable) olmasını sağlar
        return new Queue(CART_QUEUE, true);
    }
}