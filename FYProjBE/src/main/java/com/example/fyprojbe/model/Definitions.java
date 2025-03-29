package com.example.fyprojbe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "definitions")
public class Definitions {
    @Id
    private String id;
    private String word;
    private String definition;
    private String link;
}

