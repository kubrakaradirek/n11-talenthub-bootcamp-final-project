package com.n11bootcamp.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${stock.rabbit.exchange}")
    private String stockExchangeName;

    @Value("${stock.rabbit.reservedRoutingKey}")
    private String stockReservedRoutingKey;

    @Value("${stock.rabbit.rejectedRoutingKey}")
    private String stockRejectedRoutingKey;

    @Value("${order.rabbit.stockReservedQueue}")
    private String orderStockReservedQueueName;

    @Value("${order.rabbit.stockRejectedQueue}")
    private String orderStockRejectedQueueName;

    @Bean
    public TopicExchange stockEventsExchange() {
        return new TopicExchange(stockExchangeName, true, false);
    }

    @Bean
    public Queue orderStockReservedQueue() {
        return QueueBuilder.durable(orderStockReservedQueueName).build();
    }

    @Bean
    public Queue orderStockRejectedQueue() {
        return QueueBuilder.durable(orderStockRejectedQueueName).build();
    }

    @Bean
    public Binding orderStockReservedBinding(Queue orderStockReservedQueue, TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(orderStockReservedQueue)
                .to(stockEventsExchange)
                .with(stockReservedRoutingKey);
    }

    @Bean
    public Binding orderStockRejectedBinding(Queue orderStockRejectedQueue, TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(orderStockRejectedQueue)
                .to(stockEventsExchange)
                .with(stockRejectedRoutingKey);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        if (connectionFactory instanceof CachingConnectionFactory ccf) {
            ccf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
            ccf.setPublisherReturns(true);
        }

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jacksonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter);
        return factory;
    }
}
