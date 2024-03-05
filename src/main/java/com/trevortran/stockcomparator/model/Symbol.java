package com.trevortran.stockcomparator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Symbol {
    @Id
    private String ticker;
    private String name;
    private String type;
    private String region;
    private String timeZone;
    private String currency;
    private String marketOpen;
    private String marketClose;
    private LocalDate lastUpdated;
    private LocalDate ipoDate;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="stock_id_ticker", referencedColumnName = "ticker")
    private Set<Stock> stocks;

    @Override
    public int hashCode() {
        return Objects.hash(ticker, name, type, region, timeZone, currency, marketOpen, marketClose);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Symbol other)) return false;
        return this.ticker.equals(other.ticker) &&
                this.name.equals(other.name) &&
                this.region.equals(other.region) &&
                this.type.equals(other.type);
    }
}
