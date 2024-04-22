package com.trevortran.stockcomparator.alphavantage.alphaintelligence;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;
import com.trevortran.stockcomparator.model.News;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NewsProvider {

    private URL buildUrl(String ticker) throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("tickers", ticker)
                .queryParam("apikey", SecretManager.getApiKey())
                .build()
                .toUri()
                .toURL();
    }

    public List<News> request(String ticker) throws LimitExceededException {
        List<News> newsList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            URL url = buildUrl(ticker);
            NewsData response = restTemplate.getForObject(url.toString(), NewsData.class);
            assert response != null;
            String infoMessage = response.information();

            if (response.newsDetailList() != null) {
                for (NewsDetail newsDetail : response.newsDetailList()) {
                    News news = mapToNewsObject(newsDetail);
                    newsList.add(news);
                }
            } else if (infoMessage != null) {
                System.out.println(infoMessage);
                if (infoMessage.equalsIgnoreCase(Utils.INVALID_INPUTS)) {
                    log.warn("Invalid inputs for news fetch with ticker: " + ticker);
                } else {
                    throw new LimitExceededException("Rate of Requests has reached");
                }
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
