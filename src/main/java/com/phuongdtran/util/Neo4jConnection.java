package com.phuongdtran.util;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;


public class Neo4jConnection {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static String hostname;
    private static String port;
    private static String dbname;
    private static String username;
    private static String password;

    private static Driver driver;

    private Neo4jConnection(){}

    public static Driver getConnection() {
        if (driver == null) {
            String uri = String.format("bolt://%s:%s", hostname, port);
            driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "S2duyphuong"));
        }
        return driver;
    }

    public static void initialize() {
        getDbSettings();
    }

    public static void close() {
        driver.close();
        driver = null;
    }

    private static Driver getConnection(String hostname, String port, String username, String password) {
        String uri = String.format("neo4j://%s:%s", hostname, port);
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    private static void getDbSettings(){
        Properties props = new Properties();
        InputStream input = null;
        try{
            input = DatabaseConnection.class.getResourceAsStream("/config/mysql.properties");
            props.load(input);
            hostname = props.getProperty("hostname");
            port = props.getProperty("port");
            dbname = props.getProperty("dbname");
            username = props.getProperty("username");
            password = props.getProperty("password");
            if (input != null) {
                input.close();
            }
        }catch(IOException ex){
            logger.error("loading config settings failed. " + ex.getMessage());
        }
    }
}
