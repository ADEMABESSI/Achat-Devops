package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Stock;
import tn.esprit.rh.achat.services.IStockService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockRestController.class)
class StockRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IStockService stockService;

    @Test
    void getStocks_whenServiceReturnsList_returnsJsonArray() throws Exception {
        Stock s1 = new Stock();
        Stock s2 = new Stock();
        when(stockService.retrieveAllStocks()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/stock/retrieve-all-stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        verify(stockService).retrieveAllStocks();
    }

    @Test
    void getStocks_whenServiceReturnsEmpty_returnsEmptyArray() throws Exception {
        when(stockService.retrieveAllStocks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/stock/retrieve-all-stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveStock_whenFound_returnsStock() throws Exception {
        Stock stock = new Stock();
        when(stockService.retrieveStock(8L)).thenReturn(stock);

        mockMvc.perform(get("/stock/retrieve-stock/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(stockService).retrieveStock(8L);
    }

    @Test
    void addStock_whenCalled_returnsStock() throws Exception {
        Stock input = new Stock();
        when(stockService.addStock(any(Stock.class))).thenReturn(input);

        mockMvc.perform(post("/stock/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(stockService).addStock(any(Stock.class));
    }

    @Test
    void removeStock_whenCalled_returnsOk() throws Exception {
        doNothing().when(stockService).deleteStock(3L);

        mockMvc.perform(delete("/stock/remove-stock/3"))
                .andExpect(status().isOk());

        verify(stockService).deleteStock(3L);
    }

    @Test
    void modifyStock_whenCalled_returnsStock() throws Exception {
        Stock input = new Stock();
        when(stockService.updateStock(any(Stock.class))).thenReturn(input);

        mockMvc.perform(put("/stock/modify-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(stockService).updateStock(any(Stock.class));
    }
}

