package com.n11bootcamp.stock_service.service;

import com.n11bootcamp.stock_service.dto.EventPayloads;
import com.n11bootcamp.stock_service.dto.StockUpdateRequest;
import com.n11bootcamp.stock_service.dto.StockUpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockSagaHandlerTest {

    @Mock
    private StockDomainService stock;

    @Mock
    private RabbitTemplate rabbit;

    @InjectMocks
    private StockSagaHandler handler;

    @BeforeEach
    void setUp() {
        // @Value anotasyonlarını test ortamında simüle etme
        ReflectionTestUtils.setField(handler, "exchange", "stock.events.exchange");
        ReflectionTestUtils.setField(handler, "reservedRoutingKey", "order.stock.reserved");
        ReflectionTestUtils.setField(handler, "rejectedRoutingKey", "order.stock.rejected");
    }

    @Test
    void handleReserveRequested_shouldSendReservedEvent_whenStockIsAvailable() {
        // Given (Hazırlık)
        // 1. İsim 'Item' olmalı ve 'StockReserveRequestedEvent' üzerinden erişilmeli
        // 2. 'orderId' Long olduğu için '123L' gibi bir değer verilmeli
        EventPayloads.StockReserveRequestedEvent.Item payloadItem =
                new EventPayloads.StockReserveRequestedEvent.Item(1L, 2);

        EventPayloads.StockReserveRequestedEvent event =
                new EventPayloads.StockReserveRequestedEvent(123L, "kubra", List.of(payloadItem));

        // Servis rezerve işlemini BAŞARILI olarak dönecek şekilde ayarlama
        when(stock.reserve(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock reserved"));

        // When (Eylem)
        handler.handleReserveRequested(event);

        // Then (Doğrulama)
        ArgumentCaptor<EventPayloads.StockReservedEvent> captor = ArgumentCaptor.forClass(EventPayloads.StockReservedEvent.class);

        verify(rabbit, times(1)).convertAndSend(
                eq("stock.events.exchange"),
                eq("order.stock.reserved"),
                captor.capture()
        );

        EventPayloads.StockReservedEvent sentEvent = captor.getValue();
        assertEquals(123L, sentEvent.getOrderId()); // Long kontrolü
        assertEquals("kubra", sentEvent.getUsername());
    }
    @Test
    void handleReserveRequested_shouldSendRejectedEvent_whenStockIsInsufficient() {
        // Given (Hazırlık)
        // 1. Düzeltme: StockItem yerine Item kullanıyoruz ve hiyerarşiyi doğru yazma.
        EventPayloads.StockReserveRequestedEvent.Item payloadItem =
                new EventPayloads.StockReserveRequestedEvent.Item(1L, 100);

        // 2. Düzeltme: orderId "String" değil "Long" olmalı (123L gibi).
        EventPayloads.StockReserveRequestedEvent event = new EventPayloads.StockReserveRequestedEvent(
                123L, "kubra", List.of(payloadItem)
        );

        // Servis rezerve işlemini BAŞARISIZ (Insufficient stock) olarak dönecek şekilde ayarlama
        when(stock.reserve(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.fail("Insufficient stock"));

        // When (Eylem)
        handler.handleReserveRequested(event);

        // Then (Doğrulama)
        ArgumentCaptor<EventPayloads.StockRejectedEvent> captor = ArgumentCaptor.forClass(EventPayloads.StockRejectedEvent.class);

        verify(rabbit, times(1)).convertAndSend(
                eq("stock.events.exchange"),
                eq("order.stock.rejected"),
                captor.capture()
        );

        EventPayloads.StockRejectedEvent sentEvent = captor.getValue();

        // 3. Düzeltme: Assertion yaparken Long değerleri karşılaştırma.
        assertEquals(123L, sentEvent.getOrderId());
        assertEquals("kubra", sentEvent.getUsername());
        assertEquals("Insufficient stock", sentEvent.getReason());
    }
}