package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.CategorieProduit;
import tn.esprit.rh.achat.repositories.CategorieProduitRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieProduitServiceImplTest {

    @Mock
    private CategorieProduitRepository categorieProduitRepository;

    @InjectMocks
    private CategorieProduitServiceImpl categorieProduitService;

    @Test
    void retrieveAllCategorieProduits_returnsList() {
        // Test: retrieveAllCategorieProduits
        CategorieProduit cp1 = new CategorieProduit();
        CategorieProduit cp2 = new CategorieProduit();
        when(categorieProduitRepository.findAll()).thenReturn(Arrays.asList(cp1, cp2));

        java.util.List<CategorieProduit> result = categorieProduitService.retrieveAllCategorieProduits();

        assertEquals(2, result.size());
        verify(categorieProduitRepository).findAll();
    }

    @Test
    void retrieveAllCategorieProduits_empty_returnsEmptyList() {
        when(categorieProduitRepository.findAll()).thenReturn(Collections.emptyList());

        java.util.List<CategorieProduit> result = categorieProduitService.retrieveAllCategorieProduits();


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addCategorieProduit_savesAndReturnsSame() {
        CategorieProduit cp = new CategorieProduit();
        when(categorieProduitRepository.save(cp)).thenReturn(cp);

        CategorieProduit result = categorieProduitService.addCategorieProduit(cp);

        assertSame(cp, result);
        verify(categorieProduitRepository).save(cp);
    }

    @Test
    void deleteCategorieProduit_deletesById() {
        Long id = 2L;
        categorieProduitService.deleteCategorieProduit(id);
        verify(categorieProduitRepository).deleteById(id);
    }

    @Test
    void updateCategorieProduit_savesAndReturnsSaved() {
        CategorieProduit cp = new CategorieProduit();
        when(categorieProduitRepository.save(cp)).thenReturn(cp);

        CategorieProduit result = categorieProduitService.updateCategorieProduit(cp);

        assertSame(cp, result);
        verify(categorieProduitRepository).save(cp);
    }

    @Test
    void retrieveCategorieProduit_whenFound_returnsEntity() {
        Long id = 1L;
        CategorieProduit cp = new CategorieProduit();
        when(categorieProduitRepository.findById(id)).thenReturn(Optional.of(cp));

        CategorieProduit result = categorieProduitService.retrieveCategorieProduit(id);

        assertSame(cp, result);
    }

    @Test
    void retrieveCategorieProduit_whenNotFound_returnsNull() {
        Long id = 999L;
        when(categorieProduitRepository.findById(id)).thenReturn(Optional.empty());

        CategorieProduit result = categorieProduitService.retrieveCategorieProduit(id);

        assertNull(result);
    }
}

