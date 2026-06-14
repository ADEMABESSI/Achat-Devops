package tn.esprit.rh.achat.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Reglement;
import tn.esprit.rh.achat.services.IReglementService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(tn.esprit.rh.achat.controllers.ReglementRestController.class)
class ReglementRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IReglementService reglementService;

    @Test
    void getReglements_whenServiceReturnsList_returnsArray() throws Exception {
        Reglement r1 = new Reglement();
        Reglement r2 = new Reglement();
        when(reglementService.retrieveAllReglements()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/reglement/retrieve-all-reglements"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(reglementService).retrieveAllReglements();
    }

    @Test
    void getReglements_whenEmpty_returnsEmptyArray() throws Exception {
        when(reglementService.retrieveAllReglements()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reglement/retrieve-all-reglements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveReglement_whenFound_returnsReglement() throws Exception {
        Reglement r = new Reglement();
        when(reglementService.retrieveReglement(8L)).thenReturn(r);

        mockMvc.perform(get("/reglement/retrieve-reglement/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(reglementService).retrieveReglement(8L);
    }

    @Test
    void addReglement_whenCalled_returnsSavedReglement() throws Exception {
        Reglement input = new Reglement();
        when(reglementService.addReglement(any(Reglement.class))).thenReturn(input);

        mockMvc.perform(post("/reglement/add-reglement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(reglementService).addReglement(any(Reglement.class));
    }

@Test
    void removeReglement_whenCalled_returnsNotFound() throws Exception {
        // Cette route n'existe pas dans ReglementRestController
        mockMvc.perform(delete("/reglement/remove-reglement/3"))
                .andExpect(status().isNotFound());
    }

    // Cette route n'existe pas dans ReglementRestController

}

