package com.trevortran.stockcomparator.services.alphavantage;

import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.services.SymbolService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SymbolServiceImpl implements SymbolService {
    private final String ALPHA_VANTAGE_KEY = "PDOGDFRY2VU8A943";
    private final RestTemplate restTemplate;

    public SymbolServiceImpl() {
        restTemplate = new RestTemplate();
    }
    @Override
    public List<Symbol> request(String keyword) {
        List<Symbol> symbols = new ArrayList<>();
        try {
            URL url = buildUrl(keyword);
            SymbolData response = restTemplate.getForObject(url.toString(), SymbolData.class);
            if (response != null && response.rawSymbols() != null) {
                for (SymbolDetail s : response.rawSymbols()) {
                    Symbol symbol = new Symbol(s.id(), s.name(), s.type(), s.region(),
                            s.timeZone(), s.currency(), s.marketOpen(),
                            s.marketClose(), null, null);
                    symbols.add(symbol);
                }
            }
        }catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return symbols;
    }

    @Override
    public List<Symbol> request(String keyword, String region) {
        List<Symbol> symbols = request(keyword);
        return symbols.stream().filter(s -> s.getRegion().equals(region)).toList();
    }

    private URL buildUrl(String keyword) throws MalformedURLException {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query")
                .queryParam("function", "SYMBOL_SEARCH")
                .queryParam("keywords", keyword)
                .queryParam("apikey", ALPHA_VANTAGE_KEY)
                .build()
                .toUri()
                .toURL();
    }
}
