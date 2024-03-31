package com.trevortran.stockcomparator.repository;

import com.trevortran.stockcomparator.model.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, String> {

    @Query("SELECT s FROM Symbol s WHERE s.ticker LIKE :term%")
    List<Symbol> findBestMatchesById(@Param("term") String term);
}
