package com.example.fyprojbe.model;

import com.example.fyprojbe.model.types.DeckType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

// Deck object
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "decks")
public class Deck {

    @Id
    private String id;
    private String createdBy;
    private DeckType deckType;
    private String deckName;
    private List<ScryfallCard> cards;
    private ScryfallCard commander;
    private int sizeLimit;
}