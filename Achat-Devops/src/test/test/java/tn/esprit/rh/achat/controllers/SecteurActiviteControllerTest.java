package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.SecteurActivite;
import tn.esprit.rh.achat.services.ISecteurActiviteService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecteurActiviteController.class)
class SecteurActiviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISecteurActiviteService secteurActiviteService;

    @Test
    void getSecteurs_whenServiceReturnsList_returnsArray() throws Exception {
        SecteurActivite s1 = new SecteurActivite();
        SecteurActivite s2 = new SecteurActivite();
when(secteurActiviteService.retrieveAllSecteurActivite()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/secteurActivite/retrieve-all-secteurActivite"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

verify(secteurActiviteService).retrieveAllSecteurActivite();
    }

    @Test
    void getSecteurs_whenEmpty_returnsEmptyArray() throws Exception {
when(secteurActiviteService.retrieveAllSecteurActivite()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/secteurActivite/retrieve-all-secteurActivite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void retrieveSecteur_whenFound_returnsSecteur() throws Exception {
        SecteurActivite s = new SecteurActivite();
        when(secteurActiviteService.retrieveSecteurActivite(8L)).thenReturn(s);

        mockMvc.perform(get("/secteurActivite/retrieve-secteurActivite/8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(secteurActiviteService).retrieveSecteurActivite(8L);
    }

    @Test
    void addSecteur_whenCalled_returnsSavedSecteur() throws Exception {
        SecteurActivite input = new SecteurActivite();
        when(secteurActiviteService.addSecteurActivite(any(SecteurActivite.class))).thenReturn(input);

        mockMvc.perform(post("/secteurActivite/add-secteurActivite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(secteurActiviteService).addSecteurActivite(any(SecteurActivite.class));
    }

    @Test
    void removeSecteur_whenCalled_returnsOk() throws Exception {
        doNothing().when(secteurActiviteService).deleteSecteurActivite(3L);

        mockMvc.perform(delete("/secteurActivite/remove-secteurActivite/3"))
                .andExpect(status().isOk());

        verify(secteurActiviteService).deleteSecteurActivite(3L);
    }

    @Test
    void modifySecteur_whenCalled_returnsUpdatedSecteur() throws Exception {
        SecteurActivite input = new SecteurActivite();
        when(secteurActiviteService.updateSecteurActivite(any(SecteurActivite.class))).thenReturn(input);

        mockMvc.perform(put("/secteurActivite/modify-secteurActivite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(secteurActiviteService).updateSecteurActivite(any(SecteurActivite.class));
    }
}

