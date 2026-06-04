package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Deck;
import com.example.fyprojbe.model.ScryfallCard;
import com.example.fyprojbe.model.types.DeckType;
import com.example.fyprojbe.repository.DeckRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardService cardService;

    public DeckService(DeckRepository deckRepository, CardService cardService) {
        this.deckRepository = deckRepository;
        this.cardService = cardService;
    }

    // Creating a new, empty deck
    // @param deckName: Name of the deck
    // @param deckType: Type of the deck (Commander or Standard)
    public Deck createDeck(String deckName, DeckType deckType, String commanderCardName) {
        List<ScryfallCard> cards = new ArrayList<>();
        ScryfallCard commander = null;
        int sizeLimit;

        // if deck type is commander, user must enter a card
        if (deckType == DeckType.COMMANDER) {
            if (commanderCardName == null || commanderCardName.isEmpty()) {
                throw new IllegalArgumentException("Commander deck requires a commander card");
            }
            // search Scryfall for the card
            commander = cardService.searchCardByNameScryfall(commanderCardName);

            // Validate card is a legendary creature
            if (commander.getType_line() == null || !commander.getType_line().contains("Legendary Creature")) {
                throw new IllegalArgumentException("The commander must be a legendary creature. Selected card: "
                        + commander.getName() + " with type: " + commander.getType_line());
            }

            cards.add(commander); // Add commander to the card list
            sizeLimit = 100;      // Commander limit is 100 (including commander)
        } else {
            sizeLimit = 60;      // Otherwise, deck is standard with a limit of 60
        }

        // grab currently logged-in user using Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Build the deck object
        Deck deck = Deck.builder()
                .deckName(deckName)
                .createdBy(auth.getName())
                .deckType(deckType)
                .cards(cards)
                .commander(commander)
                .sizeLimit(sizeLimit)
                .build();

        return deckRepository.save(deck);
    }

    // Adding card(s) to a specific deck
    // @param deckId: ID of the deck to add cards to
    // @param cardNames: List of card names to add
    public Deck addCardToDeck(String deckId, List<String> cardNames) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));

        List<ScryfallCard> existingCards = deck.getCards();
        List<ScryfallCard> cardsToAdd = new ArrayList<>();

        // Check if adding these cards would exceed the size limit
        if (existingCards.size() + cardNames.size() > deck.getSizeLimit()) {
            throw new IllegalArgumentException("Adding these cards would exceed the deck size limit of " + deck.getSizeLimit());
        }

        // Now process each card in request (queue)
        for (String cardName : cardNames) {
            ScryfallCard cardToAdd = cardService.searchCardByNameScryfall(cardName);

            // Skip validation for Basic Lands (allowed to have multiple copies in either deck type)
            if (cardToAdd.getType_line() != null && cardToAdd.getType_line().contains("Basic Land")) {
                cardsToAdd.add(cardToAdd);
                continue;
            }

            // Now count how many of a card is already in the deck,
            // then count how many of that card is already in the cards being added,
            // and tally of total copies after adding this card
            long existingCopies = existingCards.stream()
                    .filter(existingCard -> existingCard.getName().equals(cardToAdd.getName()))
                    .count();
            long addingCopies = cardsToAdd.stream()
                    .filter(card -> card.getName().equals(cardToAdd.getName()))
                    .count();
            long totalCopies = existingCopies + addingCopies + 1;   // +1 for the current card

            if (deck.getDeckType() == DeckType.COMMANDER) {             // if deck is commander
                if (totalCopies > 1) {                                  // no duplicates allowed
                    throw new IllegalArgumentException("Commander decks cannot contain duplicate cards: " + cardToAdd.getName());
                }
            } else if (deck.getDeckType() == DeckType.STANDARD) {       // otherwise, deck is standard
                if (totalCopies > 4) {                                  // and up to 4 copies of any card allowed
                    throw new IllegalArgumentException("Standard decks cannot contain more than 4 copies of: " + cardToAdd.getName());
                }
            }

            cardsToAdd.add(cardToAdd);          // add card to the list of cards to add, loop entire queue
        }

        existingCards.addAll(cardsToAdd);       // add all cards to the existing cards
        deck.setCards(existingCards);           // set the new list of cards

        return deckRepository.save(deck);       // save the deck
    }

    // Deleting a user's deck
    public void deleteDeck(String deckId) {
        deckRepository.deleteById(deckId);
    }

    // Get deck by ID
    public Deck getDeckById(String deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));
    }

    // Get all decks created by the current user
    public List<Deck> getDecksByUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();   // Get current user from
        String username = auth.getName();                                               // Spring Security context

        return deckRepository.findByCreatedBy(username);                                // Return all decks from them
    }

    // Remove card from deck
    // @param deckId: ID of the deck to remove the card from
    // @param cardId: ID of the card to remove
    public Deck removeCardFromDeck(String deckId, String cardId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));

        List<ScryfallCard> cards = deck.getCards();

        boolean removed = false;                                // Find the first occurrence of the card with this ID
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(cardId)) {
                cards.remove(i);
                removed = true;
                break;                                          // Only remove one instance of that card at a time
            }
        }

        if (!removed) {                                         // if card not found, return error
            throw new RuntimeException("Card not found in deck");
        }

        deck.setCards(cards);                                   // set the new list of cards
        return deckRepository.save(deck);                       // save deck
    }
}