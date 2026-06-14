package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.DetailFournisseur;
import tn.esprit.rh.achat.entities.Fournisseur;
import tn.esprit.rh.achat.entities.SecteurActivite;
import tn.esprit.rh.achat.repositories.DetailFournisseurRepository;
import tn.esprit.rh.achat.repositories.FournisseurRepository;
import tn.esprit.rh.achat.repositories.ProduitRepository;
import tn.esprit.rh.achat.repositories.SecteurActiviteRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FournisseurServiceImplTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private DetailFournisseurRepository detailFournisseurRepository;

    @Mock
    private ProduitRepository produitRepository; // not used directly in tested methods

    @Mock
    private SecteurActiviteRepository secteurActiviteRepository;

    @InjectMocks
    private FournisseurServiceImpl fournisseurService;

    @Test
    void retrieveAllFournisseurs_returnsList() {
        when(fournisseurRepository.findAll()).thenReturn(Collections.emptyList());

        java.util.List<Fournisseur> result = fournisseurService.retrieveAllFournisseurs();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addFournisseur_createsDetailFournisseur_setsAndSaves() {
        Fournisseur master = new Fournisseur();
        when(fournisseurRepository.save(master)).thenReturn(master);

        Fournisseur result = fournisseurService.addFournisseur(master);

        assertSame(master, result);
        assertNotNull(master.getDetailFournisseur());
        assertTrue(master.getDetailFournisseur() instanceof DetailFournisseur);
        verify(fournisseurRepository).save(master);
    }

    @Test
    void updateFournisseur_savesDetailThenSavesFournisseur() {
        Fournisseur f = new Fournisseur();
        DetailFournisseur df = new DetailFournisseur();
        f.setDetailFournisseur(df);

        DetailFournisseur savedDf = new DetailFournisseur();
        when(detailFournisseurRepository.save(df)).thenReturn(savedDf);
        when(fournisseurRepository.save(f)).thenReturn(f);

        Fournisseur result = fournisseurService.updateFournisseur(f);

        assertSame(f, result);
        assertNotNull(f.getDetailFournisseur());
        assertTrue(f.getDetailFournisseur() instanceof DetailFournisseur);
        verify(detailFournisseurRepository).save(df);
        verify(fournisseurRepository).save(f);
    }

    @Test
    void deleteFournisseur_callsDeleteById() {
        fournisseurService.deleteFournisseur(1L);
        verify(fournisseurRepository).deleteById(1L);
    }

    @Test
    void retrieveFournisseur_whenFound_returnsEntity() {
        Fournisseur f = new Fournisseur();
        when(fournisseurRepository.findById(2L)).thenReturn(Optional.of(f));

        Fournisseur result = fournisseurService.retrieveFournisseur(2L);

        assertSame(f, result);
    }

    @Test
    void retrieveFournisseur_whenNotFound_returnsNull() {
        when(fournisseurRepository.findById(2L)).thenReturn(Optional.empty());

        Fournisseur result = fournisseurService.retrieveFournisseur(2L);

        assertNull(result);
    }

    @Test
    void assignSecteurActiviteToFournisseur_addsSecteurAndSaves() {
        Long idSecteur = 1L;
        Long idFournisseur = 2L;

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setSecteurActivites(new java.util.HashSet<>());

        SecteurActivite sa = new SecteurActivite();

        when(fournisseurRepository.findById(idFournisseur)).thenReturn(Optional.of(fournisseur));
        when(secteurActiviteRepository.findById(idSecteur)).thenReturn(Optional.of(sa));
        when(fournisseurRepository.save(fournisseur)).thenReturn(fournisseur);

        fournisseurService.assignSecteurActiviteToFournisseur(idSecteur, idFournisseur);

        assertTrue(fournisseur.getSecteurActivites().contains(sa));
        verify(fournisseurRepository).save(fournisseur);
    }
}

