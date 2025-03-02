package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeckController {

    @Autowired
    private DeckService deckService;

//    @GetMapping("/user/decks/{deckId}")
//    public Deck getDeck(String deckId) {
//        return deckService.getDeck(deckId);
//    }

    @PostMapping("/deck/create")
    public Deck createDeck(@RequestParam String deckName,
                           @RequestParam DeckType deckType) {
        return deckService.createDeck(deckName, deckType);
    }

}
