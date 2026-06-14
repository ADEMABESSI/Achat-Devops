package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.services.IOperateurService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperateurController.class)
class OperateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IOperateurService operateurService;

    @Test
    void getOperateurs_whenServiceReturnsList_returnsArray() throws Exception {
        Operateur o1 = new Operateur();
        Operateur o2 = new Operateur();
        when(operateurService.retrieveAllOperateurs()).thenReturn(Arrays.asList(o1, o2));

        mockMvc.perform(get("/operateur/retrieve-all-operateurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(operateurService).retrieveAllOperateurs();
    }

    @Test
    void getOperateurs_whenEmpty_returnsEmptyArray() throws Exception {
        when(operateurService.retrieveAllOperateurs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/operateur/retrieve-all-operateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveOperateur_whenFound_returnsOperateur() throws Exception {
        Operateur o = new Operateur();
        when(operateurService.retrieveOperateur(8L)).thenReturn(o);

        mockMvc.perform(get("/operateur/retrieve-operateur/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(operateurService).retrieveOperateur(8L);
    }

    @Test
    void addOperateur_whenCalled_returnsSavedOperateur() throws Exception {
        Operateur input = new Operateur();
        when(operateurService.addOperateur(any(Operateur.class))).thenReturn(input);

        mockMvc.perform(post("/operateur/add-operateur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(operateurService).addOperateur(any(Operateur.class));
    }

    @Test
    void removeOperateur_whenCalled_returnsOk() throws Exception {
        doNothing().when(operateurService).deleteOperateur(3L);

        mockMvc.perform(delete("/operateur/remove-operateur/3"))
                .andExpect(status().isOk());

        verify(operateurService).deleteOperateur(3L);
    }

    @Test
    void modifyOperateur_whenCalled_returnsUpdatedOperateur() throws Exception {
        Operateur input = new Operateur();
        when(operateurService.updateOperateur(any(Operateur.class))).thenReturn(input);

        mockMvc.perform(put("/operateur/modify-operateur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(operateurService).updateOperateur(any(Operateur.class));
    }
}

