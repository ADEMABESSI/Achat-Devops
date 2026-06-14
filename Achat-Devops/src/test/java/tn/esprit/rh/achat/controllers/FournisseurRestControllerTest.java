package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Fournisseur;
import tn.esprit.rh.achat.services.IFournisseurService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FournisseurRestController.class)
class FournisseurRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IFournisseurService fournisseurService;

    @Test
    void getFournisseurs_whenServiceReturnsList_returnsArray() throws Exception {
        Fournisseur f1 = new Fournisseur();
        Fournisseur f2 = new Fournisseur();
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        verify(fournisseurService).retrieveAllFournisseurs();
    }

    @Test
    void getFournisseurs_whenEmpty_returnsEmptyArray() throws Exception {
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveFournisseur_whenFound_returnsFournisseur() throws Exception {
        Fournisseur f = new Fournisseur();
        when(fournisseurService.retrieveFournisseur(8L)).thenReturn(f);

        mockMvc.perform(get("/fournisseur/retrieve-fournisseur/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(fournisseurService).retrieveFournisseur(8L);
    }

    @Test
    void addFournisseur_whenCalled_returnsSavedFournisseur() throws Exception {
        Fournisseur input = new Fournisseur();
        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(input);

        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(fournisseurService).addFournisseur(any(Fournisseur.class));
    }

    @Test
    void removeFournisseur_whenCalled_returnsOk() throws Exception {
        doNothing().when(fournisseurService).deleteFournisseur(3L);

        mockMvc.perform(delete("/fournisseur/remove-fournisseur/3"))
                .andExpect(status().isOk());

        verify(fournisseurService).deleteFournisseur(3L);
    }

    @Test
    void modifyFournisseur_whenCalled_returnsSavedFournisseur() throws Exception {
        Fournisseur input = new Fournisseur();
        when(fournisseurService.updateFournisseur(any(Fournisseur.class))).thenReturn(input);

        mockMvc.perform(put("/fournisseur/modify-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(fournisseurService).updateFournisseur(any(Fournisseur.class));
    }
}

