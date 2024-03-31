package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.Symbol;
import com.trevortran.stockcomparator.repository.SymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SymbolServiceImpl implements SymbolService{

    private final SymbolRepository symbolRepository;

    @Autowired
    public SymbolServiceImpl(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;
    }
    @Override
    public List<Symbol> findBestTickerMatches(String term) {
        return symbolRepository.findBestMatchesById(term);
    }

    @Override
    @Transactional
    public List<Symbol> save(List<Symbol> symbols) {
        return symbolRepository.saveAll(symbols);
    }

    @Override
    @Transactional
    public Symbol save(Symbol symbol) {
        return symbolRepository.save(symbol);
    }

    @Override
    public Optional<Symbol> findByTicker(String ticker) {
        return symbolRepository.findById(ticker);
    }
}
