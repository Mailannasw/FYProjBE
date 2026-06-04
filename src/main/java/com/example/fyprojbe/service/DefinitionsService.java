package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Definitions;
import com.example.fyprojbe.repository.DefinitionsRepository;
import org.springframework.stereotype.Service;

@Service
public class DefinitionsService {

    private final DefinitionsRepository definitionsRepository;

    public DefinitionsService(DefinitionsRepository definitionsRepository) {
        this.definitionsRepository = definitionsRepository;
    }

    // Get word definition
    public Definitions getDefinition(String word) {
        return definitionsRepository.findByWord(word);
    }
}
