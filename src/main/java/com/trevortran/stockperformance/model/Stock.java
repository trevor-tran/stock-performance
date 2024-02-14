package com.trevortran.stockperformance.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class Stock implements Serializable {
    @EmbeddedId
    private StockKey id;
    @NonNull
    private double endOfDayPrice;
    private double split;
    private double dividend;
}
