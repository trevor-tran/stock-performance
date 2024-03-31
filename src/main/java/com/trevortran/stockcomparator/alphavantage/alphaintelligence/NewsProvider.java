package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsProvider {

    private URL buildUrl(List<String> tickers) throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("tickers", String.join(",", tickers))
                .queryParam("apikey", SecretManager.getSecretKey())
                .build()
                .toUri()
                .toURL();
    }
}
