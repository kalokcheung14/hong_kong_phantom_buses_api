package me.kalok.busgotlost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StopEta(
        String route,
        @JsonProperty("dir")
        String direction,
        @JsonProperty("dest_tc")
        String destTc,
        @JsonProperty("dest_en")
        String destEn,
        String eta,
        @JsonProperty("eta_seq")
        String etaSeq,
        @JsonProperty("rmk_tc")
        String rmkTc,
        @JsonProperty("rmk_en")
        String rmkEn
) {

}
