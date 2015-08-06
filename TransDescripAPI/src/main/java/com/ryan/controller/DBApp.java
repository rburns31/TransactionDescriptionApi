package com.ryan.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * DBApp has support for creating, reading all of the data from, and
 *     dropping the transdescrip database, as well as creating, reading,
 *     updating, and deleting a transaction description entry in the database
 * The two instantiable sub-types of DBApp are LocalDBApp and CloudDBApp, which
 *     just change where the backing database is housed
 * This class requires setupDB.txt to be in the project's base directory
 * @author Ryan Burns
 * @version 1.0 - 6/30/2015
 */
public abstract class DBApp {
    /**
     * The SQL State error code thrown when a duplicate key is being entered
     */
    private final String duplicateSQLState;
    /**
     * The SQL State error code thrown when no results are found for the inputs
     */
    private final String noResultsSQLState;

    /**
     * Instantiates an app to access the database
     * @param duplicateSQLState The duplicate SQL State error for this database
     * @param noResultsSQLState The no results SQL State error for this database
     */
    public DBApp(String duplicateSQLState, String noResultsSQLState) {
        this.duplicateSQLState = duplicateSQLState;
        this.noResultsSQLState = noResultsSQLState;

        //createTable();
        readTable();
        //dropTable();
    }

    /**
     * Creates an entry in the database
     * @param code The transaction code passed in
     * @param language The language indicator passed in ("e" or "f")
     * @param tDescription The transaction description to store for this key
     * @return A response with an empty json body and a HTTP status code
     */
    public ResponseEntity<String> createEntry(
            int code, String language, String tDescription) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        Connection conn = null;
        try {
            conn = connect();

            // Insert the description into the database
            Statement s = conn.createStatement();
            s.executeUpdate("INSERT INTO transdescrip VALUES (" + code
                    + ", '" + language + "', '"
                    + tDescription + "')");

            shutdownServer();
        } catch (SQLException sqle) {
            //printSQLException(sqle);
            if (sqle.getSQLState().equals(duplicateSQLState)) {
                // This key (tCode, language) is already in the database
                httpStatus = HttpStatus.CONFLICT;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } finally {
            closeConnection(conn);
        }
        return new ResponseEntity<String>("{ }", httpStatus);
    }

    /**
     * Reads an entry from the database
     * @param code The transaction code passed in
     * @param language The language indicator passed in ("e" or "f")
     * @return A response with a json body and a HTTP status code
     */
    public ResponseEntity<String> readEntry(int code, String language) {
        HttpStatus httpStatus = HttpStatus.OK;
        String json = "";
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = connect();

            // Look up the description in the database
            Statement s = conn.createStatement();
            rs = s.executeQuery("SELECT description FROM transdescrip WHERE"
                    + " code=" + code + " AND language='"
                    + language + "'");
            rs.next();
            json = "{ \"description\": \"" + rs.getString(1) + "\" }";

            shutdownServer();
        } catch (SQLException sqle) {
            //printSQLException(sqle);
            if (sqle.getSQLState().equals(noResultsSQLState)) {
                // No entry in the database for the given inputs
                httpStatus = HttpStatus.BAD_REQUEST;
                json = "{ }";
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }
        return new ResponseEntity<String>(json, httpStatus);
    }

    /**
     * Updates an entry in the database
     * @param code The transaction code passed in
     * @param language The language indicator passed in ("e" or "f")
     * @param tDescription The new transaction description to store for this key
     * @return A response with an empty json body and a HTTP status code
     */
    public ResponseEntity<String> updateEntry(
            int code, String language, String tDescription) {
        HttpStatus httpStatus = HttpStatus.OK;
        Connection conn = null;
        try {
            conn = connect();

            // Update the entry in the database
            Statement s = conn.createStatement();
            int affectedRows = s.executeUpdate("UPDATE transdescrip SET "
                    + "description='" + tDescription + "' WHERE code=" + code
                    + " AND language='" + language + "'");
            if (affectedRows == 0) {
                // This key is not in the database
                httpStatus = HttpStatus.BAD_REQUEST;
            }

            shutdownServer();
        } catch (SQLException sqle) {
            //printSQLException(sqle);
            httpStatus = HttpStatus.NOT_FOUND;
        } finally {
            closeConnection(conn);
        }
        return new ResponseEntity<String>("{ }", httpStatus);
    }

    /**
     * Deletes an entry from the database
     * @param code The transaction code passed in
     * @param language The language indicator passed in ("e" or "f")
     * @return A response with an empty json body and a HTTP status code
     */
    public ResponseEntity<String> deleteEntry(int code, String language) {
        HttpStatus httpStatus = HttpStatus.OK;
        Connection conn = null;
        try {
            conn = connect();

            // Delete the entry in the database
            Statement s = conn.createStatement();
            int affectedRows = s.executeUpdate("DELETE FROM transdescrip WHERE"
                    + " code=" + code + " AND language='" + language + "'");
            if (affectedRows == 0) {
                // This key is not in the database
                httpStatus = HttpStatus.BAD_REQUEST;
            }

            shutdownServer();
        } catch (SQLException sqle) {
            //printSQLException(sqle);
            httpStatus = HttpStatus.NOT_FOUND;
        } finally {
            closeConnection(conn);
        }
        return new ResponseEntity<String>("{ }", httpStatus);
    }

    /**
     * Creates the transdescrip table from the provided 'setupDB.txt' file
     */
    public void createTable() {
        Connection conn = null;
        try {
            conn = connect();

            // Create and populate the database from file
            try {
                FileReader fileReader = new FileReader(new File(
                        "C:\\Users\\RVS935\\Documents\\workspace"
                        + "\\TransDescripAPI\\setupDB.txt"));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // Execute each line of SQL
                    Statement s = conn.createStatement();
                    s.execute(line);
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            shutdownServer();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Reads all the data from the transdescrip table and outputs it
     */
    public void readTable() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = connect();

            // Select everything and print it all out
            Statement s = conn.createStatement();
            rs = s.executeQuery("SELECT code, language, description "
                    + "FROM transdescrip ORDER BY code");
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            System.out.println();
            for (int i = 0; i < numColumns; i++) {
                if (i > 0) {
                    System.out.print(",  ");
                }
                System.out.print(rsmd.getColumnName(i + 1));
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 0; i < numColumns; i++) {
                    if (i > 0) {
                        System.out.print(",  ");
                    }
                    System.out.print(rs.getString(i + 1));
                }
                System.out.println();
            }
            System.out.println();

            shutdownServer();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }
    }

    /**
     * Drops the transdescrip table from the database
     */
    public void dropTable() {
        Connection conn = null;
        try {
            conn = connect();

            // Drop the table
            Statement s = conn.createStatement();
            s.execute("drop table transdescrip");

            shutdownServer();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeConnection(conn);
        }
    }

    abstract Connection connect() throws SQLException;

    abstract void shutdownServer();

    /**
     * Closes the ResultSet which was queried from the database
     * @param rs The ResultSet which was queried from the database
     */
    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    /**
     * Closes the connection to the database if it is open
     * @param conn The connection to the database
     */
    private void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    /**
     * Prints diagnostic information about the SQLException passed in
     * @param e The SQLException that was encountered
     */
    void printSQLException(SQLException e) {
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }
}