package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.repository.DeckRepository;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;

    @Autowired
    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Deck createDeck(String deckName, DeckType deckType) {
        List<Card> cards = new ArrayList<>();
        Deck deck = Deck.builder()
                .deckName(deckName)
                .deckType(deckType)
                .cards(cards)
                .build();
        return deckRepository.save(deck);
    }
}