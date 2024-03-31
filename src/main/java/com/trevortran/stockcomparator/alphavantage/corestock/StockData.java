package com.trevortran.stockcomparator.alphavantage.corestock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trevortran.stockcomparator.alphavantage.corestock.StockDaily;

import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockData(@JsonProperty("Monthly Adjusted Time Series") Map<LocalDate, StockDaily> data){}
