package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.Stock;

import java.time.LocalDate;
import java.util.List;

public interface StockService {

    List<Stock> save(List<Stock> stocks);
    List<Stock> findByTicker(String ticker, LocalDate start, LocalDate end);
    List<Stock> findByTickers(List<String> tickers, LocalDate start, LocalDate end);
    LocalDate findMaxDateByTicker(String ticker);
    LocalDate findMinDateByTicker(String ticker);
}
