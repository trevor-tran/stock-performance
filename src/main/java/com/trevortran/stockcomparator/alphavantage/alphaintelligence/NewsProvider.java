package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;
import com.trevortran.stockcomparator.model.News;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewsProvider {

    private URL buildUrl(String ticker) throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("tickers", ticker)
                .queryParam("apikey", SecretManager.getSecretKey())
                .build()
                .toUri()
                .toURL();
    }

    public List<News> request(String ticker) {
        List<News> newsList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            URL url = buildUrl(ticker);
            NewsData response = restTemplate.getForObject(url.toString(), NewsData.class);
            assert response != null;
            if (response.newsDetailList() != null) {
                for (NewsDetail newsDetail : response.newsDetailList()) {
                    News news = mapToNewsObject(newsDetail);
                    newsList.add(news);
                }
            } else if (response.information() != null) {
               // todo: handle limit exceed
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        return newsList;
    }

    private News mapToNewsObject(NewsDetail newsDetail) {
        // find the most relevance ticker to the news
        TickerScore tickerWithMaxScore = new TickerScore("", 0);
        for (TickerScore tickerScore : newsDetail.tickerScores()) {
            if (tickerScore.relevanceScore() > tickerWithMaxScore.relevanceScore()) {
                tickerWithMaxScore = tickerScore;
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

        return new News.Builder()
                .ticker(tickerWithMaxScore.ticker())
                .title(newsDetail.title())
                .url(newsDetail.url())
                .publishedDate(LocalDate.parse(newsDetail.timePublished(), formatter))
                .summary(newsDetail.summary())
                .imageUrl(newsDetail.bannerImage())
                .relevanceScore(tickerWithMaxScore.relevanceScore())
                .build();
    }
}
