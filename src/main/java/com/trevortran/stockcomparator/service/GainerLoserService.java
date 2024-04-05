package com.trevortran.stockcomparator.service;

import com.trevortran.stockcomparator.model.GainerLoser;

import javax.naming.LimitExceededException;
import java.util.List;

public interface GainerLoserService {
    List<GainerLoser> getTopGainers() throws LimitExceededException;
    List<GainerLoser> getTopLosers() throws LimitExceededException;
}
