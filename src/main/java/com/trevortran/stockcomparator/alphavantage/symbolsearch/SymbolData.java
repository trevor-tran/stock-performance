package com.trevortran.stockcomparator.alphavantage.symbolsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SymbolData(@JsonProperty("bestMatches") List<SymbolDetail> rawSymbols,
                         @JsonProperty("Information") String information) { }
