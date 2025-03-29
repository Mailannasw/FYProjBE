package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.repository.DeckRepository;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // logic for creating a new, empty deck
    public Deck createDeck(String deckName, DeckType deckType) {
        List<Card> cards = new ArrayList<>();

        // grab currently logged in user using Spring Security so we know who created the deck
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Deck deck = Deck.builder()
                .deckName(deckName)
                .createdBy(auth.getName())
                .deckType(deckType)
                .cards(cards)
                .build();

        return deckRepository.save(deck);
    }

    // logic for adding card(s) to a specific deck
    public Deck addCardToDeck(String deckId, List<String> cardIds) {
        Deck deck = deckRepository.findById(deckId)                           // get deck from MongoDB
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));
        List<Card> cardList = deck.getCards();                                // get cards currently in deck

        for (String cardId : cardIds) {                                       // for each card selected we want to add
            Card card = CardAPI.getCard(cardId);                              // get that card from Magic API
            cardList.add(card);                                               // add that card to card list
        }
        deck.setCards(cardList);                                              // set all added cards in the list to deck

        return deckRepository.save(deck);                                     // save updated deck to MongoDB
    }

    // logic for deleting a user's deck
    public void deleteDeck(String deckId) {
        deckRepository.deleteById(deckId);
    }
}