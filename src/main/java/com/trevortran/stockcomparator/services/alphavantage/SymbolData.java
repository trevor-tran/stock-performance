package com.trevortran.stockcomparator.services.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SymbolData(@JsonProperty("bestMatches") List<SymbolDetail> rawSymbols) { }
