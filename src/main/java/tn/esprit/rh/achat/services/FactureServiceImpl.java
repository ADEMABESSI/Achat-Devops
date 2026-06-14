package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Facture;
import tn.esprit.rh.achat.repositories.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FactureServiceImplTest {

    @Mock private FactureRepository factureRepository;
    @Mock private OperateurRepository operateurRepository;
    @Mock private DetailFactureRepository detailFactureRepository;
    @Mock private FournisseurRepository fournisseurRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private ReglementServiceImpl reglementService;

    @InjectMocks
    private FactureServiceImpl factureService;

    @Test
    void retrieveAllFactures_returnsList() {
        Facture f1 = new Facture();
        Facture f2 = new Facture();
        when(factureRepository.findAll()).thenReturn(Arrays.asList(f1, f2));
        List<Facture> result = factureService.retrieveAllFactures();
        assertEquals(2, result.size());
    }

    @Test
    void addFacture_savesAndReturns() {
        Facture f = new Facture();
        when(factureRepository.save(f)).thenReturn(f);
        Facture result = factureService.addFacture(f);
        assertSame(f, result);
        verify(factureRepository).save(f);
    }

    @Test
    void cancelFacture_marksArchiveeTrueAndUpdates() {
        Long id = 1L;
        Facture f = new Facture();
        f.setArchivee(false);
        when(factureRepository.findById(id)).thenReturn(Optional.of(f));
        factureService.cancelFacture(id);
        assertTrue(f.getArchivee());
        verify(factureRepository).save(f);
        verify(factureRepository).updateFacture(id);
    }
}
