package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Reglement;
import tn.esprit.rh.achat.repositories.FactureRepository;
import tn.esprit.rh.achat.repositories.ReglementRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReglementServiceImplTest {

    @Mock
    private FactureRepository factureRepository; // not used directly but present in dependencies

    @Mock
    private ReglementRepository reglementRepository;

    @InjectMocks
    private ReglementServiceImpl reglementService;

    @Test
    void retrieveAllReglements_returnsList() {
        Reglement r1 = new Reglement();
        Reglement r2 = new Reglement();
        when(reglementRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        java.util.List<Reglement> result = reglementService.retrieveAllReglements();

        assertEquals(2, result.size());
        verify(reglementRepository).findAll();
    }

    @Test
    void addReglement_savesAndReturns() {
        Reglement r = new Reglement();
        when(reglementRepository.save(r)).thenReturn(r);

        Reglement result = reglementService.addReglement(r);

        assertSame(r, result);
        verify(reglementRepository).save(r);
    }

    @Test
    void retrieveReglement_whenFound_returnsEntity() {
        Long id = 1L;
        Reglement r = new Reglement();
        when(reglementRepository.findById(id)).thenReturn(Optional.of(r));

        Reglement result = reglementService.retrieveReglement(id);

        assertSame(r, result);
    }

    @Test
    void retrieveReglement_whenNotFound_returnsNull() {
        when(reglementRepository.findById(1L)).thenReturn(Optional.empty());

        Reglement result = reglementService.retrieveReglement(1L);

        assertNull(result);
    }

    @Test
    void retrieveReglementByFacture_usesRepoCustomQuery() {
        long factureId = 9L;
        when(reglementRepository.retrieveReglementByFacture(factureId))
                .thenReturn(Arrays.asList(new Reglement(), new Reglement()));

        java.util.List<Reglement> result = reglementService.retrieveReglementByFacture(factureId);

        assertEquals(2, result.size());
        verify(reglementRepository).retrieveReglementByFacture(factureId);
    }

    @Test
    void getChiffreAffaireEntreDeuxDate_delegatesToRepo() {
        Date start = new Date(0);
        Date end = new Date(1000);
        when(reglementRepository.getChiffreAffaireEntreDeuxDate(start, end)).thenReturn(123.45f);

        float result = reglementService.getChiffreAffaireEntreDeuxDate(start, end);

        assertEquals(123.45f, result);
        verify(reglementRepository).getChiffreAffaireEntreDeuxDate(start, end);
    }
}

