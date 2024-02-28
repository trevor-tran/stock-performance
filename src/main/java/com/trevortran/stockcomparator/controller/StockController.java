package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockId;
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
import java.util.Optional;

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

    @GetMapping(value = "/stock/{ticker}", params = {"date"})
    public ResponseEntity<Stock> getStock(@PathVariable String ticker, @RequestParam LocalDate date) {
        StockId id = new StockId(ticker, date);
        Optional<Stock> stock = stockRepository.findById(id);
        stockService.request("aapl");
        // todo: need to research how :: works in this case
        return stock.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
            requestAndSaveStockData(ticker, start, end);
            symbol.setLastUpdated(stockRepository.findMaxDateById_Symbol(ticker));
            updateSymbol(symbol);
        }

        List<Stock> stocks = stockRepository.findStocksBySymbolIdAndDateBetween(ticker, start, end);

        return ResponseEntity.ok(stocks);
    }

    private List<Stock> requestAndSaveStockData(String ticker, LocalDate start, LocalDate end) {
        List<Stock> stocks = stockService.request(ticker, start, end);
        return stockRepository.saveAllAndFlush(stocks);
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
