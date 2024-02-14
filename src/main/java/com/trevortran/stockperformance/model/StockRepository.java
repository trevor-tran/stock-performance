package com.trevortran.stockperformance.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, StockKey> {
}
