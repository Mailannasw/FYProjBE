package com.example.fyprojbe.repository;

import com.example.fyprojbe.model.Deck;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// Uses Spring Data for MongoDB, which generates queries based on method names
// Reduces boilerplate code
public interface DeckRepository extends MongoRepository<Deck, String> {
    List<Deck> findByCreatedBy(String username);
}
