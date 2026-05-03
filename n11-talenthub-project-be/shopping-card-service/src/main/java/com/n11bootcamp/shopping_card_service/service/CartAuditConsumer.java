package com.n11bootcamp.shopping_card_service.service;

import com.n11bootcamp.shopping_card_service.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * shopping_cart_queue uzerine dusen sepet aktivite mesajlarini dinler ve
 * uygulama loguna yazar. Boylece queue mesajlari tuketilir, RabbitMQ
 * uzerinde birikmez.
 *
 * Ileride bu mesajlar baska bir audit servisine, Elasticsearch'e veya
 * analiz pipeline'ina yonlendirilebilir.
 */
@Component
public class CartAuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(CartAuditConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.CART_QUEUE)
    public void onCartActivity(String message) {
        log.info("[CART AUDIT] {}", message);
    }
}
