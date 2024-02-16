package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockKey;
import com.trevortran.stockcomparator.model.StockRepository;
import com.trevortran.stockcomparator.services.alphavantage.StockServiceImpl;
import com.trevortran.stockcomparator.services.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StockController {
    private final Logger log = LoggerFactory.getLogger(StockController.class);
    private final StockRepository stockRepository;
    private final StockService stockService;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.stockService = new StockServiceImpl();
    }

    @GetMapping(value = "/stock/{symbol}", params = {"date"})
    public ResponseEntity<Stock> getStock(@PathVariable String symbol, @RequestParam LocalDate date) {
        StockKey id = new StockKey(symbol, date);
        Optional<Stock> stock = stockRepository.findById(id);
        stockService.request("aapl");
        // todo: need to research how :: works in this case
        return stock.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/stock/{symbol}", params = {"start", "end"})
    public ResponseEntity<List<Stock>> getStockRange(@PathVariable String symbol, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        updateDbIfNeeded(symbol, start, end);
        List<Stock> stocks = stockRepository.findStocksById_SymbolAndId_DateBetween(symbol, start, end);
        return ResponseEntity.ok(stocks);
    }

    private void updateDbIfNeeded(String symbol, LocalDate start, LocalDate end) {
        List<Stock> stocks = stockService.request(symbol, start, end);
        stockRepository.saveAllAndFlush(stocks);
    }
}
