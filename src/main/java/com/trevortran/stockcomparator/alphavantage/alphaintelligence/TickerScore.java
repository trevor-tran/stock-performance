package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TickerScore(@JsonProperty("ticker") String ticker, @JsonProperty("relevance_score") double relevanceScore) {
}
