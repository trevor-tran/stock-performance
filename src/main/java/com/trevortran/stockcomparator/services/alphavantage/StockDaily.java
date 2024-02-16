package com.trevortran.stockcomparator.services.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockDaily(@JsonProperty("4. close") double price) { }
