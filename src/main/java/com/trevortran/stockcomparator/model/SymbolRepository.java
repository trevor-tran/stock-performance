package com.trevortran.stockcomparator.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SymbolRepository extends JpaRepository<Symbol, String> {

    @Query("SELECT s FROM Symbol s WHERE s.ticker LIKE %:term%")
    List<Symbol> findBestMatchesById(@Param("term") String term);
}
