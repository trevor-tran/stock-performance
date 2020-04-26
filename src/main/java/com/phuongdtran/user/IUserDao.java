package com.phuongdtran.user;

import java.sql.SQLException;

public interface IUserDao {

    void open() throws SQLException;
    boolean exists(String username) throws SQLException;
    String getFirstName(String username) throws SQLException;
    Password getPassword(String username) throws SQLException;
    boolean add(User user) throws SQLException;
    void close();
}
