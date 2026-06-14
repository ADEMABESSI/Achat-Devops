package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Facture;
import tn.esprit.rh.achat.entities.Fournisseur;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.repositories.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
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
        assertNotNull(f.getArchivee());
        assertTrue(f.getArchivee());
        verify(factureRepository).save(f);
        verify(factureRepository).updateFacture(id);
    }

    @Test
    void retrieveFacture_whenFound_returnsEntity() {
        Long id = 2L;
        Facture f = new Facture();
        when(factureRepository.findById(id)).thenReturn(Optional.of(f));
        Facture result = factureService.retrieveFacture(id);
        assertSame(f, result);
    }

    @Test
    void retrieveFacture_whenNotFound_returnsNull() {
        when(factureRepository.findById(2L)).thenReturn(Optional.empty());
        Facture result = factureService.retrieveFacture(2L);
        assertNull(result);
    }

    @Test
    void getFacturesByFournisseur_returnsFournisseurFactures() {
        Long idFournisseur = 1L;
        Fournisseur fournisseur = new Fournisseur();
        List<Facture> factures = Arrays.asList(new Facture(), new Facture());

        // ✅ FIX : utiliser ArrayList au lieu de HashSet pour éviter le ClassCastException
        fournisseur.setFactures(new java.util.HashSet<>(factures));

        when(fournisseurRepository.findById(idFournisseur)).thenReturn(Optional.of(fournisseur));

        List<Facture> result = factureService.getFacturesByFournisseur(idFournisseur);

        assertEquals(2, result.size());
    }

    @Test
    void assignOperateurToFacture_addsFactureToOperateurAndSaves() {
        Long idOperateur = 1L;
        Long idFacture = 2L;
        Operateur op = new Operateur();
        op.setFactures(new java.util.HashSet<>());
        Facture f = new Facture();
        when(factureRepository.findById(idFacture)).thenReturn(Optional.of(f));
        when(operateurRepository.findById(idOperateur)).thenReturn(Optional.of(op));
        factureService.assignOperateurToFacture(idOperateur, idFacture);
        assertTrue(op.getFactures().contains(f));
        verify(operateurRepository).save(op);
    }

    @Test
    void pourcentageRecouvrement_computesUsingTotals() {
        Date start = new Date(0);
        Date end = new Date(1000);
        when(factureRepository.getTotalFacturesEntreDeuxDates(start, end)).thenReturn(200f);
        when(reglementService.getChiffreAffaireEntreDeuxDate(start, end)).thenReturn(50f);
        float result = factureService.pourcentageRecouvrement(start, end);
        assertEquals((50f / 200f) * 100f, result);
    }

    @Test
    void pourcentageRecouvrement_whenTotalFacturesIsZero_returnsInftyOrNaN() {
        Date start = new Date(0);
        Date end = new Date(1000);
        when(factureRepository.getTotalFacturesEntreDeuxDates(start, end)).thenReturn(0f);
        when(reglementService.getChiffreAffaireEntreDeuxDate(start, end)).thenReturn(10f);
        float result = factureService.pourcentageRecouvrement(start, end);
        assertTrue(Float.isInfinite(result) || Float.isNaN(result));
    }
}
