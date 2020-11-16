package com.yuvaan.academic.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import  org.apache.commons.dbcp2.BasicDataSource;

public final class DBConnectionPool {
    private static final BasicDataSource DATA_SOURCE = new BasicDataSource();
    
    static {
        DATA_SOURCE.setUrl("jdbc:mysql://db:3306/test");
        DATA_SOURCE.setUsername("mysql");
        DATA_SOURCE.setPassword("mysql");
        DATA_SOURCE.setMaxConnLifetimeMillis(TimeUnit.MINUTES.toMillis(30));
        DATA_SOURCE.setLogExpiredConnections(false);
        DATA_SOURCE.setInitialSize(1);
        DATA_SOURCE.setMaxTotal(20);
        DATA_SOURCE.setMaxIdle(10);
        DATA_SOURCE.setMaxOpenPreparedStatements(20);
    }
    
    private DBConnectionPool() {
    
    }
    
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}