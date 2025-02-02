package com.example.fyprojbe.service;

import com.example.fyprojbe.model.response.CardResponse;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class CardService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.magicthegathering.io/v1/cards";

    public List<Card> getCards(int pageNumber, int pageSize) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("page", pageNumber)
                .queryParam("pageSize", pageSize)
                .toUriString();

        CardResponse response = restTemplate.getForObject(url, CardResponse.class);

        return response != null ? response.getCards() : List.of();
    }

    public List<Card> searchCardsByName(String cardName) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("name", cardName)
                .build()
                .toUriString();

        CardResponse response = restTemplate.getForObject(url, CardResponse.class);
        return response != null ? response.getCards() : List.of();
    }
}