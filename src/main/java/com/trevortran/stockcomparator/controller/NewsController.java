package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.News;
import com.trevortran.stockcomparator.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;


    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(value = "")
    public ResponseEntity<?> getNews(@RequestParam("tickers") List<String> tickers, @RequestParam("size") int size) {
        List<News> todayNews = new ArrayList<>();
        int numNewsPerTicker = size / tickers.size();
        for (String ticker : tickers) {
            todayNews.addAll(newsService.findLatestNewsByTicker(ticker, numNewsPerTicker));
        }
        return ResponseEntity.ok(todayNews);
    }
}
