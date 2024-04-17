package com.trevortran.stockcomparator.alphavantage.gainerloser;

import com.trevortran.stockcomparator.alphavantage.util.SecretManager;
import com.trevortran.stockcomparator.alphavantage.util.Utils;
import com.trevortran.stockcomparator.model.GainerLoser;
import org.springframework.web.client.RestTemplate;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GainerLoserProvider {
    RestTemplate restTemplate;

    public GainerLoserProvider() {
        this.restTemplate = new RestTemplate();
    }
    private URL buildUrl() throws MalformedURLException {
        return Utils.getQueryPath()
                .queryParam("function", "TOP_GAINERS_LOSERS")
                .queryParam("apikey", SecretManager.getApiKey())
                .build()
                .toUri()
                .toURL();
    }

    public List<GainerLoser> request() throws LimitExceededException {
        List<GainerLoser> gainerLosers = new ArrayList<>();
        try {
            URL url = buildUrl();
            GainerLoserData response = restTemplate.getForObject(url.toString(), GainerLoserData.class);
            assert response != null;

            if (response.information() != null) {
                throw new LimitExceededException("Rate of Requests has reached");
            }

            gainerLosers.addAll(response.topGainers());
            gainerLosers.addAll(response.topLosers());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return gainerLosers;
    }
}
