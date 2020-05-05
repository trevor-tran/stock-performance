package com.phuongdtran.stock;

import java.sql.SQLException;

public interface IStockDao {

    void open() throws SQLException;
    void close();
    boolean add(Stock stock) throws SQLException;

}
