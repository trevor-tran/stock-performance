package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.Symbol;

import java.util.List;
import java.util.Optional;

public interface SymbolService {
    List<Symbol> findBestTickerMatches(String term);
    List<Symbol> save(List<Symbol> symbols);
    Symbol save(Symbol symbol);
    Optional<Symbol> findByTicker(String ticker);
}
