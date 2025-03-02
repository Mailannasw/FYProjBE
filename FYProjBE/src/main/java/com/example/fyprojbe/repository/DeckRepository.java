package com.example.fyprojbe.repository;

import com.example.fyprojbe.model.Deck;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeckRepository extends MongoRepository<Deck, String> {
    Deck findByDeckName(String deckName);
}
