package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.service.DeckService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // get deck by id
    @GetMapping("/deck/{deckId}")
    public Deck getDeck(@PathVariable String deckId) {
        return deckService.getDeckById(deckId);
    }

    // get all decks for a user
    @GetMapping("/decks/AllUserDecks")
    public List<Deck> getAllUserDecks() {
        return deckService.getDecksByUser();
    }

    // create an (initially empty) deck
    @PostMapping("/deck/create")
    public Deck createDeck(@RequestParam String deckName,
                           @RequestParam DeckType deckType,
                           @RequestParam(required = false) String commanderCardName) {
        return deckService.createDeck(deckName, deckType, commanderCardName);
    }

    // add a card to deck
    @PostMapping("/deck/addCard")
    public Deck addCardToDeck(@RequestParam String deckId,
                              @RequestParam List<String> cardNames) {
        return deckService.addCardToDeck(deckId, cardNames);
    }

    // delete a user's deck
    @DeleteMapping("/deck/delete/{deckId}")
    public void deleteDeck(@PathVariable String deckId) {
        deckService.deleteDeck(deckId);
    }

    // remove a card from deck
    @DeleteMapping("/deck/removeCard")
    public Deck removeCardFromDeck(@RequestParam String deckId,
                                   @RequestParam String cardId) {
        return deckService.removeCardFromDeck(deckId, cardId);
    }
}
