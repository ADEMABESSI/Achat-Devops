package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Facture;
import tn.esprit.rh.achat.services.IFactureService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FactureRestController.class)
class FactureRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IFactureService factureService;

    @Test
    void getFactures_whenServiceReturnsList_returnsArray() throws Exception {
        Facture f1 = new Facture();
        Facture f2 = new Facture();
        when(factureService.retrieveAllFactures()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(get("/facture/retrieve-all-factures"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(factureService).retrieveAllFactures();
    }

    @Test
    void getFactures_whenEmpty_returnsEmptyArray() throws Exception {
        when(factureService.retrieveAllFactures()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/facture/retrieve-all-factures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveFacture_whenFound_returnsFacture() throws Exception {
        Facture f = new Facture();
        when(factureService.retrieveFacture(8L)).thenReturn(f);

        mockMvc.perform(get("/facture/retrieve-facture/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(factureService).retrieveFacture(8L);
    }

    @Test
    void addFacture_whenCalled_returnsFacture() throws Exception {
        Facture input = new Facture();
        when(factureService.addFacture(any(Facture.class))).thenReturn(input);

        mockMvc.perform(post("/facture/add-facture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(factureService).addFacture(any(Facture.class));
    }

    @Test
    void cancelFacture_whenCalled_returnsOk() throws Exception {
        doNothing().when(factureService).cancelFacture(3L);

        mockMvc.perform(put("/facture/cancel-facture/3"))
                .andExpect(status().isOk());

        verify(factureService).cancelFacture(3L);
    }

    @Test
    void getFacturesByFournisseur_whenCalled_returnsArray() throws Exception {
        Facture f1 = new Facture();
        when(factureService.getFacturesByFournisseur(2L)).thenReturn(Arrays.asList(f1));

        mockMvc.perform(get("/facture/getFactureByFournisseur/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(factureService).getFacturesByFournisseur(2L);
    }

    @Test
    void assignOperateurToFacture_whenCalled_returnsOk() throws Exception {
        doNothing().when(factureService).assignOperateurToFacture(1L, 1L);

        mockMvc.perform(put("/facture/assignOperateurToFacture/1/1"))
                .andExpect(status().isOk());

        verify(factureService).assignOperateurToFacture(1L, 1L);
    }

    @Test
    void pourcentageRecouvrement_whenServiceThrows_returnsZero() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse("2024-01-01");
        Date end = sdf.parse("2024-01-31");

        when(factureService.pourcentageRecouvrement(any(Date.class), any(Date.class))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/facture/pourcentageRecouvrement/2024-01-01/2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }

    @Test
    void pourcentageRecouvrement_whenServiceReturnsValue_returnsValue() throws Exception {
        when(factureService.pourcentageRecouvrement(any(Date.class), any(Date.class))).thenReturn(42.0f);

        mockMvc.perform(get("/facture/pourcentageRecouvrement/2024-01-01/2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("42.0"));
    }
}

