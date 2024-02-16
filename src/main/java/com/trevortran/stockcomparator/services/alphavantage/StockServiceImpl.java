package com.trevortran.stockcomparator.services.alphavantage;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockKey;
import com.trevortran.stockcomparator.services.StockService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StockServiceImpl implements StockService {
    private final String ALPHA_VANTAGE_KEY = "";
    RestTemplate restTemplate;

    public StockServiceImpl() {
        restTemplate = new RestTemplate();
    }

    private URL buildUrl(String symbol, OUTPUT_SIZE size) throws MalformedURLException {
        URL url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query")
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("outputsize", size.value)
                .queryParam("apikey", ALPHA_VANTAGE_KEY)
                .build()
                .toUri()
                .toURL();

        return url;
    }

    @Override
    public List<Stock> request(String symbol) {
        return request(symbol, OUTPUT_SIZE.FULL);
    }

    @Override
    public Stock request(String symbol, LocalDate date) {
        return null;
    }

    @Override
    public List<Stock> request(String symbol, LocalDate start, LocalDate end) {
        List<Stock> response;
        List<Stock> result = new ArrayList<>();

        LocalDate currentDate =LocalDate.now();
        long diffDate = Duration.between(currentDate, start).toDays();
        if (diffDate <= 100) {
            response = request(symbol, OUTPUT_SIZE.COMPACT);
        } else {
            response = request(symbol);
        }

        for (Stock s : response) {
            LocalDate date = s.getId().getDate();
            if (date.isAfter(start.minusDays(1)) &&
                    date.isBefore(end.plusDays(1))) {
                result.add(s);
            }
        }

        return result;
    }

    private Stock normalizeStock(String symbol, Map.Entry<LocalDate, StockDaily> dailyEntry) {
        StockKey key = new StockKey(symbol, dailyEntry.getKey());
        return new Stock(key, dailyEntry.getValue().price(), 1d, 0d);
    }

    private List<Stock> request(String symbol, OUTPUT_SIZE size) {
        List<Stock> receivedStocks = new ArrayList<>();
        try {
            URL url = buildUrl(symbol, size);
            StockData response = restTemplate.getForObject(url.toString(), StockData.class);
            if (response != null && response.data() != null) {
                for (Map.Entry<LocalDate, StockDaily> dailyStock : response.data().entrySet()) {
                    receivedStocks.add(normalizeStock(symbol, dailyStock));
                }
            }
        }catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return receivedStocks;
    }

    enum OUTPUT_SIZE {
        FULL("full"), // data history of 20 years
        COMPACT("compact"); // data history of 100 days
        final String value;
        OUTPUT_SIZE(String value) {
            this.value = value;
        }
    }
}
