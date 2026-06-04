package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.ScryfallCard;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock private DeckRepository deckRepository;
    @Mock private CardService cardService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @Spy
    @InjectMocks
    private DeckService deckService;

    private final String TEST_USERNAME = "testUser";
    private final String TEST_DECK_ID = "deck123";

    // Before each test, set up security context (lenient to avoid strict stubbing)
    // and set cardService field in DeckService
    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn(TEST_USERNAME);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ReflectionTestUtils.setField(deckService, "cardService", cardService);
    }

    @Test
    void createDeck_StandardDeck_Success() {
        // Given
        String deckName = "Test Standard Deck";
        DeckType deckType = DeckType.STANDARD;

        Deck expectedDeck = Deck.builder()
                .deckName(deckName)
                .createdBy(TEST_USERNAME)
                .deckType(deckType)
                .cards(new ArrayList<>())
                .sizeLimit(60)
                .build();

        // When
        when(deckRepository.save(any(Deck.class))).thenReturn(expectedDeck);

        Deck result = deckService.createDeck(deckName, deckType, null);

        // Then
        assertNotNull(result);
        assertEquals(deckName, result.getDeckName());
        assertEquals(TEST_USERNAME, result.getCreatedBy());
        assertEquals(deckType, result.getDeckType());
        assertEquals(60, result.getSizeLimit());
        assertTrue(result.getCards().isEmpty());
        assertNull(result.getCommander());
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void createDeck_CommanderDeck_Success() {
        // Given
        String deckName = "Test Commander Deck";
        DeckType deckType = DeckType.COMMANDER;
        String commanderName = "Atraxa, Praetors' Voice";

        ScryfallCard commander = new ScryfallCard();
        commander.setName(commanderName);
        commander.setType_line("Legendary Creature — Horror");

        List<ScryfallCard> cards = new ArrayList<>();
        cards.add(commander);

        Deck expectedDeck = Deck.builder()
                .deckName(deckName)
                .createdBy(TEST_USERNAME)
                .deckType(deckType)
                .cards(cards)
                .commander(commander)
                .sizeLimit(100)
                .build();

        // When
        when(cardService.searchCardByNameScryfall(commanderName)).thenReturn(commander);
        when(deckRepository.save(any(Deck.class))).thenReturn(expectedDeck);

        Deck result = deckService.createDeck(deckName, deckType, commanderName);

        // Then
        assertNotNull(result);
        assertEquals(deckName, result.getDeckName());
        assertEquals(TEST_USERNAME, result.getCreatedBy());
        assertEquals(deckType, result.getDeckType());
        assertEquals(100, result.getSizeLimit());
        assertEquals(1, result.getCards().size());
        assertEquals(commander, result.getCommander());
        verify(cardService).searchCardByNameScryfall(commanderName);
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void createDeck_CommanderDeck_WithoutCommander_ThrowsException() {
        // Given
        String deckName = "Test Commander Deck";
        DeckType deckType = DeckType.COMMANDER;

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deckService.createDeck(deckName, deckType, null)
        );

        // Then
        assertTrue(exception.getMessage().contains("requires a commander card"));
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void createDeck_CommanderDeck_NonLegendaryCommander_ThrowsException() {
        // Given
        String deckName = "Test Commander Deck";
        DeckType deckType = DeckType.COMMANDER;
        String commanderName = "Lightning Bolt";

        ScryfallCard nonLegendaryCard = new ScryfallCard();
        nonLegendaryCard.setName(commanderName);
        nonLegendaryCard.setType_line("Instant");

        // When
        when(cardService.searchCardByNameScryfall(commanderName)).thenReturn(nonLegendaryCard);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deckService.createDeck(deckName, deckType, commanderName)
        );

        // Then
        assertTrue(exception.getMessage().contains("must be a legendary creature"));
        verify(cardService).searchCardByNameScryfall(commanderName);
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void addCardToDeck_Success() {
        // Given
        String deckId = TEST_DECK_ID;
        List<String> cardNames = Arrays.asList("Lightning Bolt", "Counterspell");

        ScryfallCard card1 = new ScryfallCard();
        card1.setName("Lightning Bolt");
        card1.setType_line("Instant");

        ScryfallCard card2 = new ScryfallCard();
        card2.setName("Counterspell");
        card2.setType_line("Instant");

        Deck existingDeck = Deck.builder()
                .deckName("Test Deck")
                .createdBy(TEST_USERNAME)
                .deckType(DeckType.STANDARD)
                .cards(new ArrayList<>())
                .sizeLimit(60)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));
        when(cardService.searchCardByNameScryfall("Lightning Bolt")).thenReturn(card1);
        when(cardService.searchCardByNameScryfall("Counterspell")).thenReturn(card2);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck result = deckService.addCardToDeck(deckId, cardNames);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getCards().size());
        assertEquals("Lightning Bolt", result.getCards().get(0).getName());
        assertEquals("Counterspell", result.getCards().get(1).getName());
        verify(deckRepository).findById(deckId);
        verify(cardService).searchCardByNameScryfall("Lightning Bolt");
        verify(cardService).searchCardByNameScryfall("Counterspell");
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void addCardToDeck_ExceedsSizeLimit_ThrowsException() {
        // Given
        String deckId = TEST_DECK_ID;
        List<String> cardNames = Arrays.asList("Card1", "Card2");

        List<ScryfallCard> existingCards = new ArrayList<>();
        for (int i = 0; i < 59; i++) {
            ScryfallCard card = new ScryfallCard();
            card.setName("Existing Card " + i);
            existingCards.add(card);
        }

        Deck existingDeck = Deck.builder()
                .deckName("Test Deck")
                .createdBy(TEST_USERNAME)
                .deckType(DeckType.STANDARD)
                .cards(existingCards)
                .sizeLimit(60)
                .build();
        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deckService.addCardToDeck(deckId, cardNames)
        );

        // Then
        assertTrue(exception.getMessage().contains("exceed the deck size limit"));
        verify(deckRepository).findById(deckId);
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void addCardToDeck_CommanderDeckWithDuplicates_ThrowsException() {
        // Given
        String deckId = TEST_DECK_ID;
        List<String> cardNames = Arrays.asList("Lightning Bolt");

        ScryfallCard existingCard = new ScryfallCard();
        existingCard.setName("Lightning Bolt");
        existingCard.setType_line("Instant");

        ScryfallCard newCard = new ScryfallCard();
        newCard.setName("Lightning Bolt");
        newCard.setType_line("Instant");

        List<ScryfallCard> existingCards = new ArrayList<>();
        existingCards.add(existingCard);

        Deck existingDeck = Deck.builder()
                .deckName("Test Commander Deck")
                .createdBy(TEST_USERNAME)
                .deckType(DeckType.COMMANDER)
                .cards(existingCards)
                .sizeLimit(100)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));
        when(cardService.searchCardByNameScryfall("Lightning Bolt")).thenReturn(newCard);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deckService.addCardToDeck(deckId, cardNames)
        );

        // Then
        assertTrue(exception.getMessage().contains("cannot contain duplicate cards"));
        verify(deckRepository).findById(deckId);
        verify(cardService).searchCardByNameScryfall("Lightning Bolt");
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void addCardToDeck_StandardDeckWithMoreThan4Copies_ThrowsException() {
        // Given
        String deckId = TEST_DECK_ID;
        List<String> cardNames = Arrays.asList("Lightning Bolt");

        ScryfallCard boltCard = new ScryfallCard();
        boltCard.setName("Lightning Bolt");
        boltCard.setType_line("Instant");

        List<ScryfallCard> existingCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ScryfallCard card = new ScryfallCard();
            card.setName("Lightning Bolt");
            card.setType_line("Instant");
            existingCards.add(card);
        }

        Deck existingDeck = Deck.builder()
                .deckName("Test Standard Deck")
                .createdBy(TEST_USERNAME)
                .deckType(DeckType.STANDARD)
                .cards(existingCards)
                .sizeLimit(60)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));
        when(cardService.searchCardByNameScryfall("Lightning Bolt")).thenReturn(boltCard);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deckService.addCardToDeck(deckId, cardNames)
        );

        // Then
        assertTrue(exception.getMessage().contains("cannot contain more than 4 copies"));
        verify(deckRepository).findById(deckId);
        verify(cardService).searchCardByNameScryfall("Lightning Bolt");
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void deleteDeck_Success() {
        // Given
        String deckId = TEST_DECK_ID;

        // When
        deckService.deleteDeck(deckId);

        // Then
        verify(deckRepository).deleteById(deckId);
    }

    @Test
    void getDeckById_Success() {
        // Given
        String deckId = TEST_DECK_ID;
        Deck expectedDeck = Deck.builder()
                .deckName("Test Deck")
                .createdBy(TEST_USERNAME)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(expectedDeck));

        Deck result = deckService.getDeckById(deckId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDeck, result);
        verify(deckRepository).findById(deckId);
    }

    @Test
    void getDeckById_NotFound_ThrowsException() {
        // Given
        String deckId = TEST_DECK_ID;
        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(RuntimeException.class, () ->
                deckService.getDeckById(deckId)
        );

        // Then
        assertTrue(exception.getMessage().contains("Deck not found"));
        verify(deckRepository).findById(deckId);
    }

    @Test
    void getDecksByUser_Success() {
        // Given
        List<Deck> expectedDecks = Arrays.asList(
                Deck.builder().deckName("Deck 1").createdBy(TEST_USERNAME).build(),
                Deck.builder().deckName("Deck 2").createdBy(TEST_USERNAME).build()
        );

        // When
        when(deckRepository.findByCreatedBy(TEST_USERNAME)).thenReturn(expectedDecks);

        List<Deck> result = deckService.getDecksByUser();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedDecks, result);
        verify(deckRepository).findByCreatedBy(TEST_USERNAME);
    }

    @Test
    void removeCardFromDeck_Success() {
        // Given
        String deckId = TEST_DECK_ID;
        String cardId = "card123";

        ScryfallCard card1 = new ScryfallCard();
        card1.setId(cardId);
        card1.setName("Card to Remove");

        ScryfallCard card2 = new ScryfallCard();
        card2.setId("otherCard");
        card2.setName("Other Card");

        List<ScryfallCard> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);

        Deck existingDeck = Deck.builder()
                .deckName("Test Deck")
                .createdBy(TEST_USERNAME)
                .cards(cards)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck result = deckService.removeCardFromDeck(deckId, cardId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCards().size());
        assertEquals("Other Card", result.getCards().get(0).getName());
        verify(deckRepository).findById(deckId);
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void removeCardFromDeck_CardNotFound_ThrowsException() {
        // Given
        String deckId = TEST_DECK_ID;
        String cardId = "nonExistentCard";

        ScryfallCard card = new ScryfallCard();
        card.setId("differentCardId");
        card.setName("Different Card");

        List<ScryfallCard> cards = new ArrayList<>();
        cards.add(card);

        Deck existingDeck = Deck.builder()
                .deckName("Test Deck")
                .createdBy(TEST_USERNAME)
                .cards(cards)
                .build();

        // When
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(existingDeck));

        Exception exception = assertThrows(RuntimeException.class, () ->
                deckService.removeCardFromDeck(deckId, cardId)
        );

        // Then
        assertTrue(exception.getMessage().contains("Card not found in deck"));
        verify(deckRepository).findById(deckId);
        verify(deckRepository, never()).save(any(Deck.class));
    }
}