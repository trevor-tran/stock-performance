package com.trevortran.stockcomparator.alphavantage.util;

import org.springframework.web.util.UriComponentsBuilder;

public class Utils {
    public static UriComponentsBuilder getQueryPath() {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query");
    }

    public static final String LIMIT_REACH_MESSAGE = "Thank you for using Alpha Vantage! " +
            "Our standard API rate limit is 25 requests per day. " +
            "Please subscribe to any of the premium plans at https://www.alphavantage.co/premium/ " +
            "to instantly remove all daily rate limits.";

    public static final String INVALID_INPUTS = "Invalid inputs. " +
            "Please refer to the API documentation https://www.alphavantage.co/documentation#newsapi " +
            "and try again.";
}
