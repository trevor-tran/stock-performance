package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.alphavantage.gainerloser.GainerLoserProvider;
import com.trevortran.stockcomparator.model.GainerLoser;
import org.springframework.stereotype.Service;

import javax.naming.LimitExceededException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class GainerLoserServiceImpl implements GainerLoserService {
   private LocalDate lastUpdated;
   private List<GainerLoser> gainers;
   private List<GainerLoser> losers;

   private final GainerLoserProvider gainerLoserProvider;

    public GainerLoserServiceImpl() {
        gainers = new ArrayList<>();
        losers = new ArrayList<>();
        gainerLoserProvider = new GainerLoserProvider();
    }
    @Override
    public List<GainerLoser> getTopGainers() throws LimitExceededException {
        if (shouldUpdate()) {
            updateGainerAndLoserLists();
        }
       return gainers;
    }

    @Override
    public List<GainerLoser> getTopLosers() throws LimitExceededException {
        if (shouldUpdate()) {
            updateGainerAndLoserLists();
        }
        return losers;
    }

    private boolean shouldUpdate() {
        return lastUpdated == null || ChronoUnit.DAYS.between(LocalDate.now(), lastUpdated) >= 2;
    }

    private void updateGainerAndLoserLists() throws LimitExceededException {
        List<GainerLoser> gainerLosers = gainerLoserProvider.request();

        if (gainerLosers.isEmpty()) {
            return;
        }

        gainers = new ArrayList<>();
        losers = new ArrayList<>();

        for (GainerLoser gainerLoser : gainerLosers) {
            String percentageExtraction = gainerLoser.changePercentage()
                    .trim()
                    .substring(0,gainerLoser.changePercentage().length() - 1);
            double percentage = Double.parseDouble(percentageExtraction);

            if (percentage > 0) {
                gainers.add(gainerLoser);
            } else {
                losers.add(gainerLoser);
            }
        }
        lastUpdated = LocalDate.now();
    }
}
