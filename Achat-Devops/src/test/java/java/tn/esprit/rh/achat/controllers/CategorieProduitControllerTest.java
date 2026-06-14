package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.CategorieProduit;
import tn.esprit.rh.achat.services.ICategorieProduitService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategorieProduitController.class)
class CategorieProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICategorieProduitService categorieProduitService;

    @Test
    void getCategories_whenServiceReturnsList_returnsArray() throws Exception {
        CategorieProduit c1 = new CategorieProduit();
        CategorieProduit c2 = new CategorieProduit();
when(categorieProduitService.retrieveAllCategorieProduits()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/categorieProduit/retrieve-all-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

verify(categorieProduitService).retrieveAllCategorieProduits();
    }

    @Test
    void getCategories_whenServiceReturnsEmpty_returnsEmptyArray() throws Exception {
when(categorieProduitService.retrieveAllCategorieProduits()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/categorieProduit/retrieve-all-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveCategorie_whenFound_returnsCategorie() throws Exception {
        CategorieProduit c = new CategorieProduit();
        when(categorieProduitService.retrieveCategorieProduit(8L)).thenReturn(c);

        mockMvc.perform(get("/categorieProduit/retrieve-categorieProduit/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(categorieProduitService).retrieveCategorieProduit(8L);
    }

    @Test
    void addCategorie_whenCalled_returnsCategorie() throws Exception {
        CategorieProduit input = new CategorieProduit();
        when(categorieProduitService.addCategorieProduit(any(CategorieProduit.class))).thenReturn(input);

        mockMvc.perform(post("/categorieProduit/add-categorieProduit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(categorieProduitService).addCategorieProduit(any(CategorieProduit.class));
    }

    @Test
    void removeCategorie_whenCalled_returnsOk() throws Exception {
        doNothing().when(categorieProduitService).deleteCategorieProduit(3L);

        mockMvc.perform(delete("/categorieProduit/remove-categorieProduit/3"))
                .andExpect(status().isOk());

        verify(categorieProduitService).deleteCategorieProduit(3L);
    }

    @Test
    void modifyCategorie_whenCalled_returnsCategorie() throws Exception {
        CategorieProduit input = new CategorieProduit();
when(categorieProduitService.updateCategorieProduit(any(CategorieProduit.class))).thenReturn(input);

        mockMvc.perform(put("/categorieProduit/modify-categorieProduit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(categorieProduitService).updateCategorieProduit(any(CategorieProduit.class));
    }
}

