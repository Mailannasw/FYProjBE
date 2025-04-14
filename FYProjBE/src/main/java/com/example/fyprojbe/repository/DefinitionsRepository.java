package com.example.fyprojbe.repository;

import com.example.fyprojbe.model.Definitions;
import org.springframework.data.mongodb.repository.MongoRepository;

// Uses Spring Data for MongoDB, which generates queries based on method names
// Reduces boilerplate code
public interface DefinitionsRepository extends MongoRepository<Definitions, String> {
    Definitions findByWord(String word);
}
