package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.News;
import com.trevortran.stockcomparator.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

//@CrossOrigin
@CrossOrigin(origins = "https://trevortran.com, http://trevortran.com, https://www.trevortran.com, http://www.trevortran.com")
@RestController
@RequestMapping("/api/news")
@Slf4j
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
        try {
            for (String ticker : tickers) {
                todayNews.addAll(newsService.findLatestNewsByTicker(ticker, numNewsPerTicker));
            }
        } catch (LimitExceededException exception) {
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).build();
        }
        return ResponseEntity.ok(todayNews);
    }
}
