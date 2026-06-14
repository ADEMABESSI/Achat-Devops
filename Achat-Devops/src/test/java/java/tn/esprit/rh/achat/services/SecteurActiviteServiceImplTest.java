package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.SecteurActivite;
import tn.esprit.rh.achat.repositories.SecteurActiviteRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecteurActiviteServiceImplTest {

    @Mock
    private SecteurActiviteRepository secteurActiviteRepository;

    @InjectMocks
    private SecteurActiviteServiceImpl secteurActiviteService;

    @Test
    void retrieveAllSecteurActivite_returnsList() {
        when(secteurActiviteRepository.findAll()).thenReturn(Collections.emptyList());
        java.util.List<SecteurActivite> result = secteurActiviteService.retrieveAllSecteurActivite();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(secteurActiviteRepository).findAll();
    }

    @Test
    void addSecteurActivite_savesAndReturns() {
        SecteurActivite sa = new SecteurActivite();
        when(secteurActiviteRepository.save(sa)).thenReturn(sa);

        SecteurActivite result = secteurActiviteService.addSecteurActivite(sa);

        assertSame(sa, result);
        verify(secteurActiviteRepository).save(sa);
    }

    @Test
    void deleteSecteurActivite_callsDeleteById() {
        secteurActiviteService.deleteSecteurActivite(1L);
        verify(secteurActiviteRepository).deleteById(1L);
    }

    @Test
    void updateSecteurActivite_savesAndReturns() {
        SecteurActivite sa = new SecteurActivite();
        when(secteurActiviteRepository.save(sa)).thenReturn(sa);

        SecteurActivite result = secteurActiviteService.updateSecteurActivite(sa);

        assertSame(sa, result);
        verify(secteurActiviteRepository).save(sa);
    }

    @Test
    void retrieveSecteurActivite_whenFound_returnsEntity() {
        SecteurActivite sa = new SecteurActivite();
        when(secteurActiviteRepository.findById(2L)).thenReturn(Optional.of(sa));

        SecteurActivite result = secteurActiviteService.retrieveSecteurActivite(2L);

        assertSame(sa, result);
    }

    @Test
    void retrieveSecteurActivite_whenNotFound_returnsNull() {
        when(secteurActiviteRepository.findById(2L)).thenReturn(Optional.empty());

        SecteurActivite result = secteurActiviteService.retrieveSecteurActivite(2L);

        assertNull(result);
    }
}

