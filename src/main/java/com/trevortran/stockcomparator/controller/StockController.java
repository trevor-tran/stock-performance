package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.alphavantage.corestock.StockProvider;
import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.service.StockService;
import com.trevortran.stockcomparator.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class StockController {
    private final StockService stockService;
    private final SymbolService symbolService;
    private final StockProvider stockProvider = new StockProvider();

    @Autowired
    public StockController(StockService stockService, SymbolService symbolService) {
        this.stockService = stockService;
        this.symbolService = symbolService;
    }

    @GetMapping(value = "/stock/{ticker}", params = {"start", "end"})
    public ResponseEntity<List<Stock>> getStockByDateRange(@PathVariable String ticker, @RequestParam LocalDate start, @RequestParam LocalDate end) {


        Optional<Symbol> symbolOptional = symbolService.findByTicker(ticker);
        if (symbolOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LocalDate lastUpdated = symbolOptional.get().getLastUpdated();

        // fetch and save data consumed if db is out of date
        if (lastUpdated == null || lastUpdated.isBefore(end)) {
            requestAndSaveStockData(ticker);
        }

        List<Stock> stocks = stockService.findByTicker(ticker, start, end);

        return ResponseEntity.ok(stocks);
    }

    @GetMapping(value = "/stock/batch", params = {"tickers", "start", "end"})
    public ResponseEntity<List<Stock>> getStockByDateRange(@RequestParam List<String> tickers, @RequestParam LocalDate start, @RequestParam LocalDate end) {

        LocalDate mutualQueryEndDate = end;
        LocalDate mutualQueryStartDate = start;

        for (String ticker : tickers) {
            Optional<Symbol> symbolOptional = symbolService.findByTicker(ticker);
            if (symbolOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Symbol symbol = symbolOptional.get();

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


        List<Stock> stocks = stockService.findByTickers(tickers, mutualQueryStartDate, mutualQueryEndDate);

        return ResponseEntity.ok(stocks);
    }

    private void requestAndSaveStockData(String ticker) {
        List<Stock> stocks = stockProvider.request(ticker);
        stockService.save(stocks);

        Optional<Symbol> symbolOptional = symbolService.findByTicker(ticker);
        assert symbolOptional.isPresent();

        symbolOptional.ifPresent(symbol -> {
            symbol.setLastUpdated(stockService.findMaxDateByTicker(ticker));
            symbol.setIpoDate(stockService.findMinDateByTicker(ticker));
            symbolService.save(symbol);
        });
    }
}
