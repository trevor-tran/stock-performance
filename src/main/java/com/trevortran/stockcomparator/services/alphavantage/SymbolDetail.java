package com.trevortran.stockcomparator.services.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SymbolDetail(@JsonProperty("1. symbol") String id,
                           @JsonProperty("2. name") String name,
                           @JsonProperty("3. type") String type,
                           @JsonProperty("4. region") String region,
                           @JsonProperty("5. marketOpen") String marketOpen,
                           @JsonProperty("6. marketClose") String marketClose,
                           @JsonProperty("7. timezone") String timeZone,
                           @JsonProperty("8. currency") String currency) { }
