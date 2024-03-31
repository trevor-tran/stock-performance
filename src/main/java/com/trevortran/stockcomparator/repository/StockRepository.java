package com.trevortran.stockcomparator.repository;

import com.trevortran.stockcomparator.model.Stock;
import com.trevortran.stockcomparator.model.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, StockId> {
    @Query("SELECT s FROM Stock s WHERE s.id.ticker = :ticker AND s.id.date BETWEEN :start AND :end ORDER BY s.id.date")
    List<Stock> findByTickerAndDateBetween(@Param("ticker") String ticker,
                                           @Param("start")LocalDate start,
                                           @Param("end")LocalDate end);
    @Query("SELECT s FROM Stock s WHERE s.id.ticker IN :tickers AND s.id.date BETWEEN :start AND :end ORDER BY s.id.date, s.id.ticker")
    List<Stock> findByMultiTickersAndDateBetween(@Param("tickers") List<String> tickers,
                                                 @Param("start")LocalDate start,
                                                 @Param("end")LocalDate end);
    @Query("SELECT MAX(s.id.date) FROM Stock s WHERE s.id.ticker =:ticker")
    LocalDate findMaxDateByTicker(@Param("ticker") String ticker);

    @Query("SELECT MIN(s.id.date) FROM Stock s WHERE s.id.ticker =:ticker")
    LocalDate findMinDateByTicker(@Param("ticker") String ticker);
}
