package com.example.fyprojbe.model;

import com.example.fyprojbe.model.types.DeckType;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "decks")
public class Deck {

    @Id
    private String id;
    private DeckType deckType;
    private String deckName;
    private List<Card> cards;

    public Deck() {
    }

    public Deck(String id, DeckType deckType, String deckName, List<Card> cards) {
        this.id = id;
        this.deckType = deckType;
        this.deckName = deckName;
        this.cards = cards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeckType getDeckType() {
        return deckType;
    }

    public void setDeckType(DeckType deckType) {
        this.deckType = deckType;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private DeckType deckType;
        private String deckName;
        private List<Card> cards;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder deckType(DeckType deckType) {
            this.deckType = deckType;
            return this;
        }

        public Builder deckName(String deckName) {
            this.deckName = deckName;
            return this;
        }

        public Builder cards(List<Card> cards) {
            this.cards = cards;
            return this;
        }

        public Deck build() {
            Deck deck = new Deck();
            deck.id = this.id;
            deck.deckType = this.deckType;
            deck.deckName = this.deckName;
            deck.cards = this.cards;
            return deck;
        }
    }
}