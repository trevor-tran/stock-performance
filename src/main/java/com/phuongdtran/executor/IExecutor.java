package com.phuongdtran.executor;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public interface IExecutor {

    void open() throws SQLException;
    void close();
    Iterator<Map<String,Object>> query(String statement, Map<String, Object> params);
    boolean create(String statement, Map<String, Object> params);
}
