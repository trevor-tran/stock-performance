package com.trevortran.stockcomparator.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, StockKey> {
    @Query("SELECT s FROM Stock s WHERE s.id.symbol=:symbol AND s.id.date BETWEEN :start AND :end ORDER BY s.id.date ASC")
    List<Stock> findStocksById_SymbolAndId_DateBetween(@Param("symbol")String symbol,
                                                       @Param("start")LocalDate start,
                                                       @Param("end")LocalDate end);
}
