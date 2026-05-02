package com.n11bootcamp.stock_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.stock_service.dto.StockUpdateRequest;
import com.n11bootcamp.stock_service.dto.StockUpdateResponse;
import com.n11bootcamp.stock_service.service.StockDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockDomainService stockService;

    private StockUpdateRequest request;

    @BeforeEach
    void setUp() {
        StockUpdateRequest.StockItem item = new StockUpdateRequest.StockItem();
        item.setProductId(1L);
        item.setQuantity(5);

        request = new StockUpdateRequest();
        request.setItems(List.of(item));
    }

    @Test
    void decrease_shouldReturnOkAndResponse() throws Exception {
        when(stockService.decrease(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock decreased"));

        mockMvc.perform(post("/api/stocks/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock decreased"));
    }

    @Test
    void increase_shouldReturnOkAndResponse() throws Exception {
        when(stockService.increase(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock increased"));

        mockMvc.perform(post("/api/stocks/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock increased"));
    }

    @Test
    void reserve_shouldReturnOkAndResponse() throws Exception {
        when(stockService.reserve(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock reserved"));

        mockMvc.perform(post("/api/stocks/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock reserved"));
    }

    @Test
    void release_shouldReturnOkAndResponse() throws Exception {
        when(stockService.release(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock released"));

        mockMvc.perform(post("/api/stocks/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock released"));
    }

    @Test
    void commit_shouldReturnOkAndResponse() throws Exception {
        when(stockService.commit(any(StockUpdateRequest.class)))
                .thenReturn(StockUpdateResponse.ok("Stock committed"));

        mockMvc.perform(post("/api/stocks/commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock committed"));
    }
}