package com.ryan.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * LocalDBApp extends DBApp and uses a local embedded derby database
 * This class requires derby.jar to be on the build path
 * @author Ryan Burns
 * @version 1.0 - 6/30/2015
 */
public class LocalDBApp extends DBApp {

    /**
     * Passes the correct SQL State error codes for this database to DBApp
     */
    public LocalDBApp() {
        super("23505", "24000");
    }

    /**
     * Establishes a connection with the local database
     * @return The connection to the local database
     * @throws SQLException Propagate any exceptions stemming from this attempt
     */
    Connection connect() throws SQLException {
        DriverManager.registerDriver(
                new org.apache.derby.jdbc.EmbeddedDriver());
        Connection conn = DriverManager.getConnection(
                "jdbc:derby:derbyDB;create=true");
        System.out.println("Connected to local database");
        return conn;
    }

    /**
     * Shuts down the embedded derby database server
     */
    void shutdownServer() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException se) {
            if (!((se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState())))) {
                System.err.println("Derby did not shut down normally");
                super.printSQLException(se);
            }
        }
    }
}