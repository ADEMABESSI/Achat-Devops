package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.services.IProduitService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProduitRestController.class)
class ProduitRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProduitService produitService;

    @Test
    void getProduits_whenServiceReturnsList_returnsJsonArray() throws Exception {
        Produit p1 = new Produit();
        Produit p2 = new Produit();
        when(produitService.retrieveAllProduits()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/produit/retrieve-all-produits"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());

        verify(produitService, times(1)).retrieveAllProduits();
    }

    @Test
    void getProduits_whenServiceReturnsEmptyList_returnsEmptyArray() throws Exception {
        when(produitService.retrieveAllProduits()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/produit/retrieve-all-produits"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveProduit_whenFound_returnsProduit() throws Exception {
        Produit p = new Produit();
        when(produitService.retrieveProduit(8L)).thenReturn(p);

        mockMvc.perform(get("/produit/retrieve-produit/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(produitService).retrieveProduit(8L);
    }

    @Test
    void addProduit_whenCalled_returnsCreatedProduit() throws Exception {
        Produit input = new Produit();
        Produit saved = new Produit();

        when(produitService.addProduit(any(Produit.class))).thenReturn(saved);

        mockMvc.perform(post("/produit/add-produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(produitService).addProduit(any(Produit.class));
    }

    @Test
    void removeProduit_whenCalled_returnsOk() throws Exception {
        doNothing().when(produitService).deleteProduit(3L);

        mockMvc.perform(delete("/produit/remove-produit/3"))
                .andExpect(status().isOk());

        verify(produitService).deleteProduit(3L);
    }

    @Test
    void modifyProduit_whenCalled_returnsUpdatedProduit() throws Exception {
        Produit input = new Produit();
        when(produitService.updateProduit(any(Produit.class))).thenReturn(input);

        mockMvc.perform(put("/produit/modify-produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(produitService).updateProduit(any(Produit.class));
    }

    @Test
    void assignProduitToStock_whenCalled_returnsOk() throws Exception {
        doNothing().when(produitService).assignProduitToStock(1L, 5L);

        mockMvc.perform(put("/produit/assignProduitToStock/1/5"))
                .andExpect(status().isOk());

        verify(produitService).assignProduitToStock(1L, 5L);
    }
}

