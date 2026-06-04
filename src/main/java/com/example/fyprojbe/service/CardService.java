package com.example.fyprojbe.service;

import com.example.fyprojbe.model.ScryfallCard;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CardService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String scryfallApiUrl = "https://api.scryfall.com/cards/named";

    // @param cardName: Name of card sent to Scryfall API
    public ScryfallCard searchCardByNameScryfall(String cardName) {
        String url = UriComponentsBuilder.fromUriString(scryfallApiUrl)
                .queryParam("fuzzy", cardName.replace(" ", "+"))
                .build()
                .toUriString();

        try {
            return restTemplate.getForObject(url, ScryfallCard.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,       // catch NOT_FOUND (404)
                        "Either more than one card matched your search, or 0 cards matched. Try again."
                );
            }
            throw new ResponseStatusException(      // For any other HTTP error
                    e.getStatusCode(),
                    "Error from Scryfall API: " + e.getResponseBodyAsString()
            );
        } catch (Exception e) {                    // catch INTERNAL_SERVER_ERROR (500)
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + e.getMessage()
            );
        }
    }
}