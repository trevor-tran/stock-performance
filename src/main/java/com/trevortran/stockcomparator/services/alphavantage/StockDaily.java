package com.trevortran.stockcomparator.services.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockDaily(@JsonProperty("5. adjusted close") double price, @JsonProperty("7. dividend amount") double dividend) { }

