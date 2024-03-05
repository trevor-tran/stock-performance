package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockRepository;
import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.services.alphavantage.StockServiceImpl;
import com.trevortran.stockcomparator.services.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class StockController {
    private final Logger log = LoggerFactory.getLogger(StockController.class);
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final RestTemplate restTemplate;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.stockService = new StockServiceImpl();
        this.restTemplate = new RestTemplate();
    }

    @GetMapping(value = "/stock/{ticker}", params = {"start", "end"})
    public ResponseEntity<List<Stock>> getStockByDateRange(@PathVariable String ticker, @RequestParam LocalDate start, @RequestParam LocalDate end) {


        Symbol symbol = findSymbolById(ticker);
        if (symbol == null) {
            return ResponseEntity.badRequest().build();
        }

        LocalDate lastUpdated = symbol.getLastUpdated();

        // fetch and save data consumed if db is out of date
        if (lastUpdated == null || lastUpdated.isBefore(end)) {
            requestAndSaveStockData(ticker);
        }

        List<Stock> stocks = stockRepository.findByTickerAndDateBetween(ticker, start, end);

        return ResponseEntity.ok(stocks);
    }

    @GetMapping(value = "/stock/batch", params = {"tickers", "start", "end"})
    public ResponseEntity<List<Stock>> getStockByDateRange(@RequestParam List<String> tickers, @RequestParam LocalDate start, @RequestParam LocalDate end) {

        LocalDate mutualQueryEndDate = end;
        LocalDate mutualQueryStartDate = start;

        for (String ticker : tickers) {
            Symbol symbol = findSymbolById(ticker);
            if (symbol == null) {
                return ResponseEntity.badRequest().build();
            }

            LocalDate lastUpdated = symbol.getLastUpdated();
            if (lastUpdated == null || lastUpdated.isBefore(end)) {
                requestAndSaveStockData(ticker);
            }

            lastUpdated = symbol.getLastUpdated();
            LocalDate ipoDate = symbol.getIpoDate();

            assert mutualQueryEndDate != null;
            mutualQueryEndDate = mutualQueryEndDate.isAfter(lastUpdated) ? lastUpdated : mutualQueryEndDate;
            assert mutualQueryStartDate != null;
            mutualQueryStartDate = mutualQueryStartDate.isBefore(ipoDate) ? ipoDate : mutualQueryStartDate;
        }


        List<Stock> stocks = stockRepository.findByMultiTickersAndDateBetween(tickers, mutualQueryStartDate, mutualQueryEndDate);

        return ResponseEntity.ok(stocks);
    }

    private void requestAndSaveStockData(String ticker) {
        List<Stock> stocks = stockService.request(ticker);
        stockRepository.saveAllAndFlush(stocks);
        Symbol symbol = findSymbolById(ticker);
        symbol.setLastUpdated(stockRepository.findMaxDateByTicker(ticker));
        symbol.setIpoDate(stockRepository.findMinDateByTicker(ticker));
        updateSymbol(symbol);
    }

    private void updateSymbol(Symbol symbol) {
        final String url = "http://localhost:8080/api/symbol";
        restTemplate.put(url, symbol);
    }

    private Symbol findSymbolById(String symbolId) {
        final String url = "http://localhost:8080/api/symbol/" + symbolId;
        return restTemplate.getForObject(url, Symbol.class);
    }
}
