package com.trevortran.stockcomparator.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, StockKey> {
}
