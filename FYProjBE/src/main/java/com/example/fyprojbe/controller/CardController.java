package com.example.fyprojbe.controller;

import com.example.fyprojbe.service.CardService;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    // Get specific card by ID
    @GetMapping("/card/{cardId}")
    public Card getCard(@PathVariable String cardId) {
        return CardAPI.getCard(cardId);
    }

    // Get all cards, paginated
    @GetMapping("/cards")
    public List<Card> getCards(@RequestParam(defaultValue = "1") int pageNumber,
                               @RequestParam(defaultValue = "10") int pageSize) {
        return cardService.getCards(pageNumber, pageSize);
    }

    // Get cards by name, exact or partial
    @GetMapping("/cards/search")
    public List<Card> getCardsByName(@RequestParam String cardName) {
        return cardService.searchCardsByName(cardName);
    }

}
