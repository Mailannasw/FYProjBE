package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.ScryfallCard;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.service.DeckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeckControllerTest {

    private MockMvc mockMvc;
    @Mock private DeckService deckService;
    @InjectMocks private DeckController deckController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Build deck controller before each test
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(deckController).build();
    }

    @Test
    void getDeck_Success() throws Exception {
        // Given
        String deckId = "deck123";
        Deck mockDeck = buildMockDeck(deckId, "Test Deck", DeckType.STANDARD);

        // When
        when(deckService.getDeckById(deckId)).thenReturn(mockDeck);

        mockMvc.perform(get("/deck/{deckId}", deckId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(deckId)))
                .andExpect(jsonPath("$.deckName", is("Test Deck")))
                .andExpect(jsonPath("$.deckType", is("STANDARD")));

        // Then
        verify(deckService).getDeckById(deckId);
    }

    @Test
    void getMyDecks_Success() throws Exception {
        // Given
        List<Deck> mockDecks = Arrays.asList(
                buildMockDeck("deck1", "First Deck", DeckType.STANDARD),
                buildMockDeck("deck2", "Second Deck", DeckType.COMMANDER)
        );

        // When
        when(deckService.getDecksByUser()).thenReturn(mockDecks);

        mockMvc.perform(get("/decks/AllUserDecks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("deck1")))
                .andExpect(jsonPath("$[0].deckName", is("First Deck")))
                .andExpect(jsonPath("$[1].id", is("deck2")))
                .andExpect(jsonPath("$[1].deckName", is("Second Deck")));

        // Then
        verify(deckService).getDecksByUser();
    }

    @Test
    void createDeck_StandardDeck_Success() throws Exception {
        // Given
        String deckName = "New Standard Deck";
        DeckType deckType = DeckType.STANDARD;
        Deck mockDeck = buildMockDeck("newDeck", deckName, deckType);

        // When
        when(deckService.createDeck(eq(deckName), eq(deckType), isNull())).thenReturn(mockDeck);

        mockMvc.perform(post("/deck/create")
                        .param("deckName", deckName)
                        .param("deckType", deckType.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("newDeck")))
                .andExpect(jsonPath("$.deckName", is(deckName)))
                .andExpect(jsonPath("$.deckType", is("STANDARD")));

        // Then
        verify(deckService).createDeck(deckName, deckType, null);
    }

    @Test
    void createDeck_CommanderDeck_Success() throws Exception {
        // Given
        String deckName = "New Commander Deck";
        DeckType deckType = DeckType.COMMANDER;
        String commanderName = "Atraxa, Praetors' Voice";

        Deck mockDeck = buildMockDeck("newDeck", deckName, deckType);
        ScryfallCard commander = new ScryfallCard();
        commander.setName(commanderName);
        mockDeck.setCommander(commander);

        // When
        when(deckService.createDeck(deckName, deckType, commanderName)).thenReturn(mockDeck);

        mockMvc.perform(post("/deck/create")
                        .param("deckName", deckName)
                        .param("deckType", deckType.toString())
                        .param("commanderCardName", commanderName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("newDeck")))
                .andExpect(jsonPath("$.deckName", is(deckName)))
                .andExpect(jsonPath("$.deckType", is("COMMANDER")))
                .andExpect(jsonPath("$.commander.name", is(commanderName)));

        // Then
        verify(deckService).createDeck(deckName, deckType, commanderName);
    }

    @Test
    void addCardToDeck_Success() throws Exception {
        // Given
        String deckId = "deck123";
        List<String> cardNames = Arrays.asList("Lightning Bolt", "Counterspell");

        Deck mockDeck = buildMockDeck(deckId, "Test Deck", DeckType.STANDARD);
        ScryfallCard card1 = new ScryfallCard();
        card1.setName("Lightning Bolt");
        card1.setId("card1");

        ScryfallCard card2 = new ScryfallCard();
        card2.setName("Counterspell");
        card2.setId("card2");

        mockDeck.setCards(Arrays.asList(card1, card2));

        // When
        when(deckService.addCardToDeck(deckId, cardNames)).thenReturn(mockDeck);

        mockMvc.perform(post("/deck/addCard")
                        .param("deckId", deckId)
                        .param("cardNames", "Lightning Bolt", "Counterspell")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(deckId)))
                .andExpect(jsonPath("$.cards", hasSize(2)))
                .andExpect(jsonPath("$.cards[0].name", is("Lightning Bolt")))
                .andExpect(jsonPath("$.cards[1].name", is("Counterspell")));

        // Then
        verify(deckService).addCardToDeck(deckId, cardNames);
    }

    @Test
    void deleteDeck_Success() throws Exception {
        // Given
        String deckId = "deck123";

        // When
        doNothing().when(deckService).deleteDeck(deckId);

        mockMvc.perform(delete("/deck/delete/{deckId}", deckId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        verify(deckService).deleteDeck(deckId);
    }

    @Test
    void removeCardFromDeck_Success() throws Exception {
        // Given
        String deckId = "deck123";
        String cardId = "card1";

        Deck mockDeck = buildMockDeck(deckId, "Test Deck", DeckType.STANDARD);
        ScryfallCard remainingCard = new ScryfallCard();
        remainingCard.setName("Counterspell");
        remainingCard.setId("card2");
        mockDeck.setCards(Collections.singletonList(remainingCard));

        // When
        when(deckService.removeCardFromDeck(deckId, cardId)).thenReturn(mockDeck);

        mockMvc.perform(delete("/deck/removeCard")
                        .param("deckId", deckId)
                        .param("cardId", cardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(deckId)))
                .andExpect(jsonPath("$.cards", hasSize(1)))
                .andExpect(jsonPath("$.cards[0].name", is("Counterspell")));

        // Then
        verify(deckService).removeCardFromDeck(deckId, cardId);
    }

    // Helper method to build mock decks
    private Deck buildMockDeck(String id, String name, DeckType type) {
        Deck deck = new Deck();
        deck.setId(id);
        deck.setDeckName(name);
        deck.setDeckType(type);
        deck.setCreatedBy("testUser");
        deck.setCards(new ArrayList<>());
        return deck;
    }
}