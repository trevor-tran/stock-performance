package com.phuongdtran.executor;

import com.phuongdtran.util.Neo4jConnection;
import com.phuongdtran.util.ConnectionManager;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class CytherExecutor implements IExecutor {

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Connection conn;
    private Driver driver;
    private Session session;

    public CytherExecutor() {
        driver = Neo4jConnection.getConnection();

    }

    @Override
    public void open() throws SQLException {
        if (driver == null) {
            throw new SQLException("Could not make a connection to database");
        }
        session = driver.session();
//        conn = ConnectionManager.getInstance().getConnection();


//        if (conn == null) {
//            throw new SQLException("Could not make a connection to database");
//        }
    }

    @Override
    public void close(){
//        ConnectionManager.getInstance().releaseConnection(conn);
        session.close();
    }

    @Override
    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
        final Result result = session.run(query, params);
        return new ResultIterator(result);
    }


    @Override
    public boolean create(String query, Map<String, Object> params) {
        return session.writeTransaction( tx ->  {
            try {
                tx.run(query, params);
                return true;
            }catch (Exception e) {
                return false;
            }
        });
    }


    static class ResultIterator implements Iterator<Map<String, Object>> {
        private Result result;

        ResultIterator(Result result) {
            this.result = result;
        }

        @Override
        public boolean hasNext() {
            return result.hasNext();
        }

        @Override
        public Map<String, Object> next() {
            return result.next().asMap();
        }

        @Override
        public void remove() { }
    }

//    @Override
//    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
//        try {
//            final PreparedStatement statement = conn.prepareStatement(query);
//            setParameters(statement, params);
//            final ResultSet result = statement.executeQuery();
//            return new Iterator<Map<String, Object>>() {
//
//                boolean hasNext = result.next();
//                public List<String> columns;
//
//                @Override
//                public boolean hasNext() {
//                    return hasNext;
//                }
//
//                private List<String> getColumns() throws SQLException {
//                    if (columns != null) return columns;
//                    ResultSetMetaData metaData = result.getMetaData();
//                    int count = metaData.getColumnCount();
//                    List<String> cols = new ArrayList<>(count);
//                    for (int i = 1; i <= count; i++) cols.add(metaData.getColumnName(i));
//                    return columns = cols;
//                }
//
//                @Override
//                public Map<String, Object> next() {
//                    try {
//                        if (hasNext) {
//                            Map<String, Object> map = new LinkedHashMap<>();
//                            for (String col : getColumns()) map.put(col, result.getObject(col));
//                            hasNext = result.next();
//                            if (!hasNext) {
//                                result.close();
//                                statement.close();
//                            }
//                            return map;
//                        } else throw new NoSuchElementException();
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                @Override
//                public void remove() {
//                }
//            };
//        } catch (SQLException ex) {
//            logger.error(ex.getMessage());
//            refreshConnection();
//            return null;
//        }
//    }

    private void refreshConnection(){
        ConnectionManager.getInstance().releaseConnection(conn);
        conn = ConnectionManager.getInstance().getConnection();
    }

    private void setParameters(PreparedStatement statement, Map<String, Object> params) throws SQLException {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            statement.setObject(index, entry.getValue());
        }
    }
}
