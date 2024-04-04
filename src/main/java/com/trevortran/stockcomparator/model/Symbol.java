package com.trevortran.stockcomparator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
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
    @JoinColumn(name="ticker")
    private Set<Stock> stocks;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="ticker")
    private Set<Stock> news;

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

    public static class Builder {
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
        private Set<Stock> stocks;
        private Set<Stock> news;

        public Builder ticker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder timeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder marketClose(String marketClose) {
            this.marketClose = marketClose;
            return this;
        }

        public Builder marketOpen(String marketOpen) {
            this.marketOpen = marketOpen;
            return this;
        }

        public Builder lastUpdated(LocalDate lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder ipoDate(LocalDate ipoDate) {
            this.ipoDate = ipoDate;
            return this;
        }

        public Symbol build() {
            return new Symbol(this);
        }
    }

    private Symbol(Builder builder) {
        ticker = builder.ticker;
        name = builder.name;
        type = builder.type;
        region = builder.region;
        timeZone = builder.timeZone;
        currency = builder.currency;
        marketOpen = builder.marketOpen;
        marketClose = builder.marketClose;
        lastUpdated = builder.lastUpdated;
        ipoDate = builder.ipoDate;
    }
}
