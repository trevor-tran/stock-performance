package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.News;

import javax.naming.LimitExceededException;
import java.util.List;

public interface NewsService {
    News save(News news);
    List<News> save(List<News> newsList);
    List<News> findLatestNewsByTicker(String ticker, int top) throws LimitExceededException;
}
