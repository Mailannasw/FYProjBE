package com.example.fyprojbe.service;

import com.example.fyprojbe.model.ScryfallCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock private RestTemplate restTemplate;
    @InjectMocks private CardService cardService;

    // Before each test, set field in CardService to use mock RestTemplate
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cardService, "restTemplate", restTemplate);
    }

    @Test
    void searchCardByNameScryfall_ShouldReturnCard_WhenCardExists() {
        // Given
        String cardName = "Black Lotus";
        ScryfallCard expectedCard = new ScryfallCard();
        expectedCard.setName(cardName);

        // When
        when(restTemplate.getForObject(contains("fuzzy=Black+Lotus"), eq(ScryfallCard.class)))
                .thenReturn(expectedCard);

        ScryfallCard result = cardService.searchCardByNameScryfall(cardName);

        // Then
        assertNotNull(result);
        assertEquals(cardName, result.getName());
    }

    @Test
    void searchCardByNameScryfall_ShouldThrowResponseStatusException_WhenCardNotFound() {
        // Given
        String cardName = "Nonexistent Card";

        // When
        when(restTemplate.getForObject(contains("fuzzy=Nonexistent+Card"), eq(ScryfallCard.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                cardService.searchCardByNameScryfall(cardName)
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Either more than one card matched your search, or 0 cards matched. Try again."));
    }

    @Test
    void searchCardByNameScryfall_ShouldThrowResponseStatusException_WhenOtherHttpClientError() {
        // Given
        String cardName = "Problem Card";

        // When
        when(restTemplate.getForObject(contains("fuzzy=Problem+Card"), eq(ScryfallCard.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                cardService.searchCardByNameScryfall(cardName)
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error from Scryfall API"));
    }

    @Test
    void searchCardByNameScryfall_ShouldThrowResponseStatusException_WhenGeneralException() {
        // Given
        String cardName = "Exception Card";

        // When
        when(restTemplate.getForObject(contains("fuzzy=Exception+Card"), eq(ScryfallCard.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                cardService.searchCardByNameScryfall(cardName)
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("An unexpected error occurred"));
    }
}