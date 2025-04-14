package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.ScryfallCard;
import com.example.fyprojbe.service.CardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Controller for handling card-related requests
@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Get cards by name from Scryfall API, exact or partial matches (supported by Scryfall)
    @GetMapping("/cards/search")
    public ScryfallCard getCardsByName(@RequestParam String cardName) {
        System.out.println("Searching for card: " + cardName);
        try {
            ScryfallCard card = cardService.searchCardByNameScryfall(cardName);
            System.out.println("Card found: " + card.getName());
            return card;
        } catch (ResponseStatusException e) {
            System.out.println("Card not found: " + e.getStatusCode() + " - " + e.getReason());
            throw e;
        }
    }
}
