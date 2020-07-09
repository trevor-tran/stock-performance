package com.phuongdtran.stock;

import java.sql.SQLException;
import java.util.List;

public interface IStockDao {

    void open() throws SQLException;
    void close();
    boolean add(Stock stock) throws SQLException;
    Ticker getSymbolInfo(String symbol) throws SQLException;
    List<Stock> get(String symbol, String startDate, String endDate) throws SQLException;
}
