package me.kalok.busgotlost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListResult<T> (
    String type,
    String version,
    @JsonProperty("generated_timestamp")
    String generatedTimestamp,
    List<T> data
) {

}
