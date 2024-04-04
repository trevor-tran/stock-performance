package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsDetail(@JsonProperty("title") String title,
                         @JsonProperty("url") String url,
                         @JsonProperty("time_published") String timePublished,
                         @JsonProperty("summary") String summary,
                         @JsonProperty("banner_image") String bannerImage,
                         @JsonProperty("ticker_sentiment") List<TickerScore> tickerScores) {
}
