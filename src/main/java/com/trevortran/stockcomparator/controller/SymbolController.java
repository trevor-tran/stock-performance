package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.alphavantage.symbolsearch.SymbolProvider;
import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.service.SymbolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.LimitExceededException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api")
public class SymbolController {
    private final SymbolService symbolService;

    private final SymbolProvider symbolProvider = new SymbolProvider();

    @Autowired
    public SymbolController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @GetMapping(value = "/symbol/{id}")
    public ResponseEntity<Symbol> getSymbolById(@PathVariable String id) {
        Optional<Symbol> foundSymbol = symbolService.findByTicker(id);
        return foundSymbol.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/symbol", params = {"keyword"})
    public ResponseEntity<List<Symbol>> findMatches(@RequestParam String keyword) {
        if (keyword.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Symbol> symbolMatches = symbolService.findBestTickerMatches(keyword);
        try {
            if (symbolMatches.isEmpty()) {
                symbolMatches = fetchAndUpdateDb(keyword);
            }
        } catch (LimitExceededException exception) {
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).build();
        }
        return ResponseEntity.ok(symbolMatches);
    }

    @PutMapping(value = "/symbol")
    public Symbol update(@RequestBody Symbol symbol) {
        return symbolService.save(symbol);
    }

    private List<Symbol> fetchAndUpdateDb(String keyword) throws LimitExceededException {
        List<Symbol> symbols = symbolProvider.request(keyword, "United States");
        return symbolService.save(symbols);
    }
}
