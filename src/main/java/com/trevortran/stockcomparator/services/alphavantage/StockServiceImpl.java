package com.trevortran.stockcomparator.services.alphavantage;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockId;
import com.trevortran.stockcomparator.services.StockService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;


public class StockServiceImpl implements StockService {
    private final String ALPHA_VANTAGE_KEY = "";
    private final RestTemplate restTemplate;

    public StockServiceImpl() {
        restTemplate = new RestTemplate();
    }

    private URL buildUrl(String ticker) throws MalformedURLException {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query")
                .queryParam("function", "TIME_SERIES_MONTHLY_ADJUSTED")
                .queryParam("symbol", ticker)
                .queryParam("apikey", ALPHA_VANTAGE_KEY)
                .build()
                .toUri()
                .toURL();
    }

    @Override
    public List<Stock> request(String ticker) {
        List<Stock> receivedStocks = new ArrayList<>();
        try {
            URL url = buildUrl(ticker);
            StockData response = restTemplate.getForObject(url.toString(), StockData.class);
            if (response != null && response.data() != null) {
                for (Map.Entry<LocalDate, StockDaily> dailyStock : response.data().entrySet()) {
                    receivedStocks.add(normalizeStock(ticker, dailyStock));
                }
            }
        }catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return receivedStocks;
    }

    private Stock normalizeStock(String ticker, Map.Entry<LocalDate, StockDaily> dailyEntry) {
        StockId key = new StockId(ticker, dailyEntry.getKey());
        return new Stock(key, dailyEntry.getValue().price(), dailyEntry.getValue().dividend());
    }

}
