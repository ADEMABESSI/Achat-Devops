package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.repositories.OperateurRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperateurServiceImplTest {

    @Mock
    private OperateurRepository operateurRepository;

    @InjectMocks
    private OperateurServiceImpl operateurService;

    @Test
    void retrieveAllOperateurs_returnsList() {
        when(operateurRepository.findAll()).thenReturn(Collections.emptyList());

        java.util.List<Operateur> result = operateurService.retrieveAllOperateurs();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(operateurRepository).findAll();
    }

    @Test
    void addOperateur_savesAndReturns() {
        Operateur op = new Operateur();
        when(operateurRepository.save(op)).thenReturn(op);

        Operateur result = operateurService.addOperateur(op);

        assertSame(op, result);
        verify(operateurRepository).save(op);
    }

    @Test
    void deleteOperateur_callsDeleteById() {
        operateurService.deleteOperateur(3L);
        verify(operateurRepository).deleteById(3L);
    }

    @Test
    void updateOperateur_savesAndReturns() {
        Operateur op = new Operateur();
        when(operateurRepository.save(op)).thenReturn(op);

        Operateur result = operateurService.updateOperateur(op);

        assertSame(op, result);
        verify(operateurRepository).save(op);
    }

    @Test
    void retrieveOperateur_whenFound_returnsEntity() {
        Operateur op = new Operateur();
        when(operateurRepository.findById(10L)).thenReturn(Optional.of(op));

        Operateur result = operateurService.retrieveOperateur(10L);

        assertSame(op, result);
    }

    @Test
    void retrieveOperateur_whenNotFound_returnsNull() {
        when(operateurRepository.findById(10L)).thenReturn(Optional.empty());

        Operateur result = operateurService.retrieveOperateur(10L);

        assertNull(result);
    }
}

