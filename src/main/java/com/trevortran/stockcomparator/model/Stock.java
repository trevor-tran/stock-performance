package com.trevortran.stockcomparator.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class Stock implements Serializable {
    @EmbeddedId
    private StockKey id;
    private double endOfDayPrice;
    private double split = 1.0d;
    private double dividend = 0d;
}
