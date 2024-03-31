package com.trevortran.stockcomparator.alphavantage.corestock;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;
import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockId;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockProvider {

    private URL buildUrl(String ticker) throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "TIME_SERIES_MONTHLY_ADJUSTED")
                .queryParam("symbol", ticker)
                .queryParam("apikey", SecretManager.getSecretKey())
                .build()
                .toUri()
                .toURL();
    }


    public List<Stock> request(String ticker) {
        List<Stock> receivedStocks = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            URL url = buildUrl(ticker);
            StockData response = restTemplate.getForObject(url.toString(), StockData.class);
            if (response != null && response.data() != null) {
                for (Map.Entry<LocalDate, StockDaily> dailyStock : response.data().entrySet()) {
                    receivedStocks.add(normalizeStock(ticker, dailyStock));
                }
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return receivedStocks;
    }

    private Stock normalizeStock(String ticker, Map.Entry<LocalDate, StockDaily> dailyEntry) {
        StockId key = new StockId(ticker, dailyEntry.getKey());
        return new Stock(key, dailyEntry.getValue().price(), dailyEntry.getValue().dividend());
    }
}
