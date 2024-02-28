package com.trevortran.stockcomparator.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    @Query("SELECT s FROM Stock s WHERE s.id.ticker = :ticker AND s.id.date BETWEEN :start AND :end ORDER BY s.id.date ASC")
    List<Stock> findStocksBySymbolIdAndDateBetween(@Param("ticker") String ticker,
                                                   @Param("start")LocalDate start,
                                                   @Param("end")LocalDate end);
    @Query("SELECT MAX(s.id.date) FROM Stock s WHERE s.id.ticker =:ticker")
    LocalDate findMaxDateById_Symbol(@Param("ticker") String ticker);
}
