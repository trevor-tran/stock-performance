package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.model.SymbolRepository;
import com.trevortran.stockcomparator.services.SymbolService;
import com.trevortran.stockcomparator.services.alphavantage.SymbolServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(SymbolController.class);
    private final SymbolRepository symbolRepository;
    private final SymbolService symbolService;

    public SymbolController(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;
        this.symbolService = new SymbolServiceImpl();
    }

    @GetMapping(value = "/symbol/{id}")
    public ResponseEntity<Symbol> getSymbolById(@PathVariable String id) {
        Optional<Symbol> foundSymbol = symbolRepository.findById(id);
        return foundSymbol.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/symbol", params = {"keyword"})
    public ResponseEntity<List<Symbol>> findMatches(@RequestParam String keyword) {
        List<Symbol> symbolMatches = symbolRepository.findBestMatchesById(keyword);
        if (symbolMatches.isEmpty()) {
            List<Symbol> fetchedSymbols = fetchAndUpdateDb(keyword);
            symbolMatches = merge(symbolMatches, fetchedSymbols);
        }
        return ResponseEntity.ok(symbolMatches);
    }

    @PutMapping(value = "/symbol")
    public Symbol update(@RequestBody Symbol symbol) {
        return symbolRepository.saveAndFlush(symbol);
    }

    private List<Symbol> fetchAndUpdateDb(String keyword) {
        List<Symbol> symbols = symbolService.request(keyword, "United States");
        return symbolRepository.saveAllAndFlush(symbols);
    }

    private List<Symbol> merge(List<Symbol> list1, List<Symbol> list2) {
        Set<Symbol> symbols = new HashSet<>();
        symbols.addAll(list1);
        symbols.addAll(list2);
        return symbols.stream().toList();
    }
}
