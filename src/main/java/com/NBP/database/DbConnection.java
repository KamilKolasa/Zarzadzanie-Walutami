package com.NBP.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    private static DbConnection instance = null;

    private static Connection connection;

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/exchange_rate_NBP";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static Connection getInstance(){
        if (instance == null){
            instance = new DbConnection();
        }
        return connection;
    }

    private DbConnection(){
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            createTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(){
        try {
            String sqlExchangeRate = "CREATE TABLE IF NOT EXISTS Exchange_rate" +
                    "( " +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "currency VARCHAR(50) NOT NULL, " +
                    "symbol VARCHAR(3) NOT NULL, " +
                    "exchange_today DOUBLE NOT NULL, " +
                    "exchange_yesterday DOUBLE NOT NULL " +
                    " );";
            Statement statement = connection.createStatement();
            statement.execute(sqlExchangeRate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
