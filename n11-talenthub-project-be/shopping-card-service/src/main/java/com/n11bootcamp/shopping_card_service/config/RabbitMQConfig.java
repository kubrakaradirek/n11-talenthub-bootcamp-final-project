package com.n11bootcamp.shopping_card_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String CART_QUEUE = "shopping_cart_queue";

    @Bean
    public Queue cartQueue() {
        // true parametresi kuyrugun kalici (durable) olmasini saglar
        return new Queue(CART_QUEUE, true);
    }

    /**
     * Sepet mesajlari String olarak gonderildigi icin SimpleMessageConverter
     * yeterli. Ileride POJO/JSON gonderirsen Jackson2JsonMessageConverter'a
     * gec.
     */
    @Bean
    public MessageConverter cartMessageConverter() {
        return new SimpleMessageConverter();
    }

    /**
     * @RabbitListener anotasyonunun kullandigi factory. Bean ismi
     * "rabbitListenerContainerFactory" olmasi onemli.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter cartMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(cartMessageConverter);
        return factory;
    }
}
