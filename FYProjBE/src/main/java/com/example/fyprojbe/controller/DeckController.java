package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeckController {

    @Autowired
    private DeckService deckService;

//    @GetMapping("/user/decks/{deckId}")
//    public Deck getDeck(String deckId) {
//        return deckService.getDeck(deckId);
//    }

    // create an empty deck
    @PostMapping("/deck/create")
    public Deck createDeck(@RequestParam String deckName,
                           @RequestParam DeckType deckType) {
        return deckService.createDeck(deckName, deckType);
    }

    // add a card to a specific deck
    @PostMapping("/deck/addCard")
    public Deck addCardToDeck(@RequestParam String deckId,
                              @RequestParam List<String> cardIds) {
        return deckService.addCardToDeck(deckId, cardIds);
    }

    // delete a user's deck
    @DeleteMapping("/deck/delete/{deckId}")
    public void deleteDeck(@PathVariable String deckId) {
        deckService.deleteDeck(deckId);
    }
}
