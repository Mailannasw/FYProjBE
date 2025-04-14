package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Definitions;
import com.example.fyprojbe.service.DefinitionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefinitionsController {

    private final DefinitionsService definitionsService;

    public DefinitionsController(DefinitionsService definitionsService) {
        this.definitionsService = definitionsService;
    }

    // Get definition of searched word
    @GetMapping("/definition/{word}")
    public Definitions getDefinition(@PathVariable String word) {
        return definitionsService.getDefinition(word);
    }
}
