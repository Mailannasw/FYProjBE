package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.Definitions;
import com.example.fyprojbe.service.DefinitionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefinitionsController {

    @Autowired
    private DefinitionsService definitionsService;

    // Get definition of searched word
    @GetMapping("/definition/{word}")
    public Definitions getDefinition(@PathVariable String word) {
        return definitionsService.getDefinition(word);
    }
}
