package com.trevortran.stockcomparator.repository;

import com.trevortran.stockcomparator.model.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    List<News> findByTickerAndPublishedDateOrderByRelevanceScoreDesc(String ticker, LocalDate publishedDate, Pageable pageable);
    List<News> findByTickerOrderByPublishedDateDescRelevanceScoreDesc(String ticker, Pageable pageable);
    boolean existsNewsByUrl(String url);
}
