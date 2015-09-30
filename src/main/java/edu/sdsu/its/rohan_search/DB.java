package edu.sdsu.its.rohan_search;

import edu.sdsu.its.rohan_search.models.File;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Tom Paulus
 *         Created on 9/23/15.
 */
public class DB {
    private static DB instance;
    private static Connection connection;

    /**
     * Create DB Instance.
     * Will create the table if it does not exist.
     * The DB stores the name of the file, its size, its path, and the last time it was indexed.
     */
    private DB() {
        try {
            connection = getConnection();

        } catch (SQLException e) {
            Logger.getLogger(getClass()).fatal("Problem connecting to DB.", e);
        }
    }

    /**
     * Connect to DB.
     *
     * @return {@link Connection} The Connection to the Database
     * @throws SQLException {@link SQLException} If the DB cannot be reached, or if there is a problem communicating with the DB.
     */
    private static Connection getConnection() throws SQLException {
        String username = new Config().getDb_user();
        String password = new Config().getDb_password();
        String dbUrl = "jdbc:postgresql://" + new Config().getDb_host() + "/" + new Config().getDb_name();
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("ssl", "true");
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(getInstance().getClass()).fatal("Driver not found", e);
        }
        return DriverManager.getConnection(dbUrl, props);
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }

        return instance;
    }


    /**
     * Search for File Name in File DB
     *
     * @param query {@link String} String to Search For
     * @return {@link List} List of File {@link File} Objects that match the supplied query.
     */
    public List<File> search(final String query) {
        List<File> files = new ArrayList<>();
        try {
            String sql = "SELECT *\n" +
                    "FROM files where (file_name ~* '" + query + "')\n" +
                    "ORDER BY file_name ASC;";

            Logger.getLogger(getClass()).info(String.format("Making SQL Call - %s", sql));

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                File new_file = new File();
                new_file.setFile_name(resultSet.getString("file_name"));
                new_file.setFile_path(resultSet.getString("file_path"));
                new_file.setFile_size(resultSet.getString("file_size"));

                files.add(new_file);
            }

        } catch (SQLException e) {
            Logger.getLogger(getClass()).error("Problem Communicating with DB", e);
        }

        return files;
    }
}

