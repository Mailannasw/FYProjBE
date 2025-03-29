package com.example.fyprojbe.service;

import com.example.fyprojbe.model.Definitions;
import com.example.fyprojbe.repository.DefinitionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefinitionsService {

    private final DefinitionsRepository definitionsRepository;

    @Autowired
    public DefinitionsService(DefinitionsRepository definitionsRepository) {
        this.definitionsRepository = definitionsRepository;
    }

    public Definitions getDefinition(String word) {
        return definitionsRepository.findByWord(word);
    }
}
