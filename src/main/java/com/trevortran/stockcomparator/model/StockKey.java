package com.trevortran.stockcomparator.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StockKey implements Serializable {
    @NonNull
    private String symbol;
    @NonNull
    private LocalDate date;
}
