package com.trevortran.stockcomparator.services;

import com.trevortran.stockcomparator.model.Symbol;

import java.util.List;

public interface SymbolService {

    List<Symbol> request(String keyword);
    List<Symbol> request(String keyword, String region);
}
