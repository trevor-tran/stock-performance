package com.trevortran.stockcomparator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GainerLoser(@JsonProperty("ticker") String ticker,
                          @JsonProperty("price") double price,
                          @JsonProperty("change_amount") double changeAmount,
                          @JsonProperty("change_percentage") double changePercentage) {
}
