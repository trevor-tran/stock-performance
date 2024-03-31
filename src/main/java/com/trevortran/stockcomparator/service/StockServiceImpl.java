package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class StockServiceImpl implements StockService {

    private StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public List<Stock> save(List<Stock> stocks) {
        return stockRepository.saveAll(stocks);
    }

    @Override
    public List<Stock> findByTicker(String ticker, LocalDate start, LocalDate end) {
        return stockRepository.findByTickerAndDateBetween(ticker, start, end);
    }

    @Override
    public List<Stock> findByTickers(List<String> tickers, LocalDate start, LocalDate end) {
        return stockRepository.findByMultiTickersAndDateBetween(tickers, start, end);
    }

    @Override
    public LocalDate findMaxDateByTicker(String ticker) {
        return stockRepository.findMaxDateByTicker(ticker);
    }

    @Override
    public LocalDate findMinDateByTicker(String ticker) {
        return stockRepository.findMinDateByTicker(ticker);
    }

}
