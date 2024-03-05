package com.trevortran.stockcomparator.services.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockData(@JsonProperty("Monthly Adjusted Time Series") Map<LocalDate, StockDaily> data){}