package me.kalok.busgotlost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stop(
        @JsonProperty("stop")
        String stopId,
        @JsonProperty("name_en")
        String nameEn,
        @JsonProperty("name_tc")
        String nameTc,
        @JsonProperty("lat")
        String latitude,
        @JsonProperty("long")
        String longitude
) {
}
