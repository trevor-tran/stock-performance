package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsData(@JsonProperty("feed") List<NewsDetail> newsDetailList, @JsonProperty("Information") String information) {
}
