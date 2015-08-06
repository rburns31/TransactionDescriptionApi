package com.ryan.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CloudDBApp extends DBApp and uses an AWS RDS database
 * This class requires mysql-connector.jar to be on the build path
 * @author Ryan Burns
 * @version 1.0 - 6/30/2015
 */
public class CloudDBApp extends DBApp {

    /**
     * Passes the correct SQL State error codes for this database to DBApp
     */
    public CloudDBApp() {
        super("23000", "S1000");
    }

    /**
     * Establishes a connection with the cloud database
     * @return The connection to the cloud database
     * @throws SQLException Propagate any exceptions stemming from this attempt
     */
    Connection connect() throws SQLException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        String url = "jdbc:mysql://ryandb.crv1otzbekk9.us-east-1.rds.amazonaws"
                + ".com:3306/transdescrip?user=rvs935&password=Rb653481";
        Connection conn = DriverManager.getConnection(url);
        System.out.println("Connected to AWS database");
        return conn;
    }

    /**
     * Do nothing because you can't shut down an AWS server
     */
    void shutdownServer() { }
}