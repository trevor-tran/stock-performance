package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.alphavantage.alphaintelligence.NewsProvider;
import com.trevortran.stockcomparator.model.News;
import com.trevortran.stockcomparator.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.LimitExceededException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsProvider newsProvider = new NewsProvider();

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    @Override
    @Transactional
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    @Transactional
    public List<News> save(List<News> newsList) {
        return newsRepository.saveAll(newsList);
    }

    @Override
    public List<News> findLatestNewsByTicker(String ticker, int size) throws LimitExceededException {
        LocalDate today = LocalDate.now().minusDays(1);

        List<News> todayNews = newsRepository.findByTickerAndPublishedDateOrderByRelevanceScoreDesc(
                ticker,
                today,
                PageRequest.ofSize(size)
        );

        if (todayNews.size() != size) {
            List<News> requestedNewsList = newsProvider.request(ticker);
            List<News> newsList = new ArrayList<>();
            for (News news : requestedNewsList) {
                if (!newsRepository.existsNewsByUrl(news.getUrl())) {
                    newsList.add(news);
                }
            }
            newsRepository.saveAllAndFlush(newsList);
        }

        return newsRepository.findByTickerOrderByPublishedDateDescRelevanceScoreDesc(
                ticker,
                PageRequest.ofSize(size)
        );
    }
}
