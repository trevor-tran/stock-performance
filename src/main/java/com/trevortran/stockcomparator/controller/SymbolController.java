package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.alphavantage.symbolsearch.SymbolProvider;
import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
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
        List<Symbol> symbolMatches = symbolService.findBestTickerMatches(keyword);
        if (symbolMatches.isEmpty()) {
            List<Symbol> fetchedSymbols = fetchAndUpdateDb(keyword);
            symbolMatches = merge(symbolMatches, fetchedSymbols);
        }
        return ResponseEntity.ok(symbolMatches);
    }

    @PutMapping(value = "/symbol")
    public Symbol update(@RequestBody Symbol symbol) {
        return symbolService.save(symbol);
    }

    private List<Symbol> fetchAndUpdateDb(String keyword) {
        List<Symbol> symbols = symbolProvider.request(keyword, "United States");
        return symbolService.save(symbols);
    }

    private List<Symbol> merge(List<Symbol> list1, List<Symbol> list2) {
        Set<Symbol> symbols = new HashSet<>();
        symbols.addAll(list1);
        symbols.addAll(list2);
        return symbols.stream().toList();
    }
}
