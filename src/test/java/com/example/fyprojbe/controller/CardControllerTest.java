package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.ScryfallCard;
import com.example.fyprojbe.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private MockMvc mockMvc;
    @Mock private CardService cardService;
    @InjectMocks private CardController cardController;

    // Build card controller before each test
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @Test
    void getCardsByName_CardExists_ReturnsCard() throws Exception {
        // Given
        ScryfallCard card = new ScryfallCard();
        card.setName("Lightning Bolt");
        card.setType_line("Instant");

        // When
        when(cardService.searchCardByNameScryfall("Lightning Bolt")).thenReturn(card);

        mockMvc.perform(get("/cards/search")
                        .param("cardName", "Lightning Bolt")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Lightning Bolt")))
                .andExpect(jsonPath("$.type_line", is("Instant")));

        // Then
        verify(cardService).searchCardByNameScryfall("Lightning Bolt");
    }

    @Test
    void getCardsByName_CardDoesNotExist_ReturnsNotFound() throws Exception {
        // Given
        String cardName = "NonexistentCard";

        // When
        when(cardService.searchCardByNameScryfall(cardName))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        mockMvc.perform(get("/cards/search")
                        .param("cardName", cardName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Then
        verify(cardService).searchCardByNameScryfall(cardName);
    }
}