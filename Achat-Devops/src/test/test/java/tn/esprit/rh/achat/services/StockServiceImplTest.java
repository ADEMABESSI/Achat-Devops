package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Stock;
import tn.esprit.rh.achat.repositories.StockRepository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void retrieveAllStocks_whenRepoReturnsList_returnsList() {
        // Test: retrieveAllStocks
        // Input: repo returns list
        // Expected: returns same list
        Stock s1 = new Stock();
        Stock s2 = new Stock();
        when(stockRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Stock> result = stockService.retrieveAllStocks();

        assertEquals(2, result.size());
        verify(stockRepository).findAll();
    }

    @Test
    void retrieveAllStocks_whenRepoReturnsEmpty_returnsEmptyList() {
        // Test: retrieveAllStocks
        // Input: empty list
        // Expected: empty list
        when(stockRepository.findAll()).thenReturn(Collections.emptyList());

        List<Stock> result = stockService.retrieveAllStocks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addStock_whenCalled_returnsSaved() {
        // Test: addStock
        Stock s = new Stock();
        when(stockRepository.save(s)).thenReturn(s);

        Stock result = stockService.addStock(s);

        assertSame(s, result);
        verify(stockRepository).save(s);
    }

    @Test
    void deleteStock_whenCalled_callsDeleteById() {
        // Test: deleteStock
        Long id = 7L;

        stockService.deleteStock(id);

        verify(stockRepository).deleteById(id);
    }

    @Test
    void updateStock_whenCalled_returnsSaved() {
        // Test: updateStock
        Stock s = new Stock();
        when(stockRepository.save(s)).thenReturn(s);

        Stock result = stockService.updateStock(s);

        assertSame(s, result);
        verify(stockRepository).save(s);
    }

    @Test
    void retrieveStock_whenFound_returnsEntity() {
        // Test: retrieveStock
        Long id = 1L;
        Stock s = new Stock();
        when(stockRepository.findById(id)).thenReturn(Optional.of(s));

        Stock result = stockService.retrieveStock(id);

        assertSame(s, result);
    }

    @Test
    void retrieveStock_whenNotFound_returnsNull() {
        // Test: retrieveStock
        Long id = 404L;
        when(stockRepository.findById(id)).thenReturn(Optional.empty());

        Stock result = stockService.retrieveStock(id);

        assertNull(result);
    }

    @Test
    void retrieveStatusStock_whenRepoReturnsStocks_containsExpectedLines() {
        // Test: retrieveStatusStock
        // Input: repo.retrieveStatusStock returns 1 stock with required fields
        // Expected: returned message contains libelleStock and qte/qteMin
        Stock s = new Stock();
        s.setLibelleStock("Stock-A");
        s.setQte(3);
        s.setQteMin(10);

        when(stockRepository.retrieveStatusStock()).thenReturn(Collections.singletonList(s));

        String msg = stockService.retrieveStatusStock();

        assertNotNull(msg);
        assertTrue(msg.contains("le stock Stock-A"));
        assertTrue(msg.contains("quantité de 3"));
        assertTrue(msg.contains("inférieur à la quantité minimale"));
        assertTrue(msg.contains("10"));
    }

    @Test
    void retrieveStatusStock_whenRepoReturnsEmpty_returnsEmptyString() {
        // Test: retrieveStatusStock
        when(stockRepository.retrieveStatusStock()).thenReturn(Collections.emptyList());

        String msg = stockService.retrieveStatusStock();

        // Implementation returns finalMessage initial value ("")
        assertNotNull(msg);
        assertEquals("", msg);
    }
}

