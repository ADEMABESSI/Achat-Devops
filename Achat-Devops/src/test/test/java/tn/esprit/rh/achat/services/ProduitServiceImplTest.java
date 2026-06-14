package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.entities.Stock;
import tn.esprit.rh.achat.repositories.CategorieProduitRepository;
import tn.esprit.rh.achat.repositories.ProduitRepository;
import tn.esprit.rh.achat.repositories.StockRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CategorieProduitRepository categorieProduitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    @Test
    void retrieveAllProduits_whenRepoReturnsList_returnsList() {
        // Test: retrieveAllProduits
        // Input: none
        // Expected: returns repo list
        Produit p1 = new Produit();
        Produit p2 = new Produit();
        when(produitRepository.findAll()).thenReturn(java.util.Arrays.asList(p1, p2));

        java.util.List<Produit> result = produitService.retrieveAllProduits();

        assertEquals(2, result.size());
        verify(produitRepository).findAll();
    }

    @Test
    void retrieveAllProduits_whenRepoReturnsEmptyList_returnsEmptyList() {
        // Test: retrieveAllProduits
        // Input: none
        // Expected: returns empty list
        when(produitRepository.findAll()).thenReturn(Collections.emptyList());

        java.util.List<Produit> result = produitService.retrieveAllProduits();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addProduit_whenCalled_savesAndReturnsSameEntity() {
        // Test: addProduit
        // Input: Produit p
        // Expected: repository.save called and returned instance
        Produit p = new Produit();
        when(produitRepository.save(p)).thenReturn(p);

        Produit result = produitService.addProduit(p);

        assertSame(p, result);
        verify(produitRepository).save(p);
    }

    @Test
    void retrieveProduit_whenFound_returnsProduit() {
        // Test: retrieveProduit
        // Input: produitId
        // Expected: repo.findById called and returns entity
        Long id = 10L;
        Produit p = new Produit();
        when(produitRepository.findById(id)).thenReturn(Optional.of(p));

        Produit result = produitService.retrieveProduit(id);

        assertSame(p, result);
        verify(produitRepository).findById(id);
    }

    @Test
    void retrieveProduit_whenNotFound_returnsNull() {
        // Test: retrieveProduit
        // Input: produitId
        // Expected: null when Optional empty
        Long id = 999L;
        when(produitRepository.findById(id)).thenReturn(Optional.empty());

        Produit result = produitService.retrieveProduit(id);

        assertNull(result);
    }

    @Test
    void updateProduit_whenCalled_returnsSavedEntity() {
        // Test: updateProduit
        // Input: Produit p
        // Expected: save called, return saved entity
        Produit p = new Produit();
        when(produitRepository.save(p)).thenReturn(p);

        Produit result = produitService.updateProduit(p);

        assertSame(p, result);
        verify(produitRepository).save(p);
    }

    @Test
    void deleteProduit_whenCalled_deletesById() {
        // Test: deleteProduit
        // Input: produitId
        // Expected: repository.deleteById called
        Long id = 3L;

        produitService.deleteProduit(id);

        verify(produitRepository).deleteById(id);
    }

    @Test
    void assignProduitToStock_whenBothEntitiesFound_setsStockAndSavesProduit() {
        // Test: assignProduitToStock
        // Input: idProduit, idStock
        // Expected: produit.setStock(stock) and save
        Long idProduit = 1L;
        Long idStock = 5L;

        Produit produit = new Produit();
        Stock stock = new Stock();

        when(produitRepository.findById(idProduit)).thenReturn(Optional.of(produit));
        when(stockRepository.findById(idStock)).thenReturn(Optional.of(stock));
        when(produitRepository.save(produit)).thenReturn(produit);

        produitService.assignProduitToStock(idProduit, idStock);

        assertSame(stock, produit.getStock());
        verify(produitRepository).save(produit);
    }

    @Test
    void assignProduitToStock_whenProduitNotFound_doesNotSave() {
        // Test: assignProduitToStock
        // Input: produit not found
        // Expected: no save (current implementation would NPE; we validate it throws)
        Long idProduit = 1L;
        Long idStock = 5L;

        when(produitRepository.findById(idProduit)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> produitService.assignProduitToStock(idProduit, idStock));
        verify(produitRepository, never()).save(any());
    }
}

