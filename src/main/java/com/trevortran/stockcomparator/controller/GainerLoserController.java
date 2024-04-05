package com.trevortran.stockcomparator.controller;

import com.trevortran.stockcomparator.model.GainerLoser;
import com.trevortran.stockcomparator.service.GainerLoserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.LimitExceededException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/top-stock")
public class GainerLoserController {
    private final GainerLoserService gainerLoserService;

    @Autowired
    public GainerLoserController(GainerLoserService gainerLoserService) {
        this.gainerLoserService = gainerLoserService;
    }
    @GetMapping("/gainers")
    public ResponseEntity<?> getGainersHandler() {
        try {
            List<GainerLoser> gainers = gainerLoserService.getTopGainers();
            return ResponseEntity.ok(gainers);
        } catch (LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).build();
        }
    }

    @GetMapping("/losers")
    public ResponseEntity<?> getLosersHandler() {
        try {
            List<GainerLoser> losers = gainerLoserService.getTopLosers();
            return ResponseEntity.ok(losers);
        } catch (LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).build();
        }
    }
}
