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
    private final String ALPHA_VANTAGE_KEY = "<removed>";
    private final RestTemplate restTemplate;

    public StockServiceImpl() {
        restTemplate = new RestTemplate();
    }

    private URL buildUrl(String ticker, OUTPUT_SIZE size) throws MalformedURLException {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("query")
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", ticker)
                .queryParam("outputsize", size.value)
                .queryParam("apikey", ALPHA_VANTAGE_KEY)
                .build()
                .toUri()
                .toURL();
    }

    @Override
    public List<Stock> request(String ticker) {
        return request(ticker, OUTPUT_SIZE.FULL);
    }

    @Override
    public Stock request(String ticker, LocalDate date) {
        return null;
    }

    @Override
    public List<Stock> request(String ticker, LocalDate start, LocalDate end) {
        List<Stock> response;
        List<Stock> result = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        long diffDate = DAYS.between(start, currentDate);
        if (diffDate <= 100) {
            response = request(ticker, OUTPUT_SIZE.COMPACT);
        } else {
            response = request(ticker);
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

    private Stock normalizeStock(String ticker, Map.Entry<LocalDate, StockDaily> dailyEntry) {
        StockId key = new StockId(ticker, dailyEntry.getKey());
        return new Stock(key, dailyEntry.getValue().price(), 1d, 0d);
    }

    private List<Stock> request(String ticker, OUTPUT_SIZE size) {
        List<Stock> receivedStocks = new ArrayList<>();
        try {
            URL url = buildUrl(ticker, size);
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

    enum OUTPUT_SIZE {
        FULL("full"), // data history of 20 years
        COMPACT("compact"); // data history of 100 days
        final String value;
        OUTPUT_SIZE(String value) {
            this.value = value;
        }
    }
}
