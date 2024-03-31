package com.trevortran.stockcomparator.alphavantage.util;

import org.springframework.web.util.UriComponentsBuilder;

public class Utils {


    public static UriComponentsBuilder getQueryPath() {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query");
    }
}
