package com.example.fyprojbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;

// Scryfall object (Scryfall, 2025)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScryfallCard {
    private String object;
    private String id;
    private String oracle_id;
    private List<Integer> multiverse_ids;
    private Integer mtgo_id;
    private Integer mtgo_foil_id;
    private Integer tcgplayer_id;
    private Integer cardmarket_id;
    private String name;
    private String lang;
    private String released_at;
    private String uri;
    private String scryfall_uri;
    private String layout;
    private boolean highres_image;
    private String image_status;
    private Map<String, String> image_uris;
    private String mana_cost;
    private Double cmc;
    private String type_line;
    private String oracle_text;
    private String power;
    private String toughness;
    private List<String> colors;
    private List<String> color_identity;
    private List<String> keywords;
    private Map<String, String> legalities;
    private List<String> games;
    private boolean reserved;
    private boolean game_changer;
    private boolean foil;
    private boolean nonfoil;
    private List<String> finishes;
    private boolean oversized;
    private boolean promo;
    private boolean reprint;
    private boolean variation;
    private String set_id;
    private String set;
    private String set_name;
    private String set_type;
    private String set_uri;
    private String set_search_uri;
    private String scryfall_set_uri;
    private String rulings_uri;
    private String prints_search_uri;
    private String collector_number;
    private boolean digital;
    private String rarity;
    private String flavor_text;
    private String card_back_id;
    private String artist;
    private List<String> artist_ids;
    private String illustration_id;
    private String border_color;
    private String frame;
    private String security_stamp;
    private boolean full_art;
    private boolean textless;
    private boolean booster;
    private boolean story_spotlight;
    private Integer edhrec_rank;
    private Integer penny_rank;
    private Map<String, String> prices;
    private Map<String, String> related_uris;
    private Map<String, String> purchase_uris;
}