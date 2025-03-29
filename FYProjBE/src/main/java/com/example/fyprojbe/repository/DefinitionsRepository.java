package com.example.fyprojbe.repository;

import com.example.fyprojbe.model.Definitions;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DefinitionsRepository extends MongoRepository<Definitions, String> {
    Definitions findByWord(String word);
}
