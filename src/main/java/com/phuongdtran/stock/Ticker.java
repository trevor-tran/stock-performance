package com.phuongdtran.stock;
import lombok.Getter;

public class Ticker {
    @Getter
    private String symbol;
    @Getter
    private String ipo;
    @Getter
    private String delisting;

    public Ticker(String symbol, String ipo, String delisting) {
        this.symbol = symbol;
        this.ipo = ipo;
        this.delisting = delisting;
    }
}
