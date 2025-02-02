package com.example.fyprojbe.model.response;

import io.magicthegathering.javasdk.resource.Card;
import java.util.List;

public class CardResponse {
    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
