package com.trevortran.stockcomparator.alphavantage.gainerloser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trevortran.stockcomparator.model.GainerLoser;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record GainerLoserData(@JsonProperty("top_gainers") List<GainerLoser> topGainers,
                       @JsonProperty("top_losers") List<GainerLoser> topLosers,
                       @JsonProperty("Information") String information) {
}
