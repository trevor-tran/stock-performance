package com.trevortran.stockcomparator.alphavantage.symbolsearch;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;
import com.trevortran.stockcomparator.model.Symbol;
import org.springframework.web.client.RestTemplate;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SymbolProvider {
    private final RestTemplate restTemplate;

    public SymbolProvider() {
        restTemplate = new RestTemplate();
    }
    public List<Symbol> request(String keyword) throws LimitExceededException {
        List<Symbol> symbols = new ArrayList<>();
        try {
            URL url = buildUrl(keyword);
            SymbolData response = restTemplate.getForObject(url.toString(), SymbolData.class);
            assert response != null;

            if (response.rawSymbols() != null) {
                for (SymbolDetail s : response.rawSymbols()) {
                    Symbol symbol = new Symbol.Builder()
                            .ticker(s.id())
                            .name(s.name())
                            .type(s.type())
                            .region(s.region())
                            .timeZone(s.timeZone())
                            .currency(s.currency())
                            .marketOpen(s.marketOpen())
                            .marketClose(s.marketClose())
                            .build();
                    symbols.add(symbol);
                }
            } else if (response.information() != null) {
                throw new LimitExceededException("Rate of Requests has reached");
            }
        }catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return symbols;
    }

    public List<Symbol> request(String keyword, String region) throws LimitExceededException {
        List<Symbol> symbols = request(keyword);
        return symbols.stream().filter(s -> s.getRegion().equals(region)).toList();
    }

    private URL buildUrl(String keyword) throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "SYMBOL_SEARCH")
                .queryParam("keywords", keyword)
                .queryParam("apikey", SecretManager.getApiKey())
                .build()
                .toUri()
                .toURL();
    }
}
