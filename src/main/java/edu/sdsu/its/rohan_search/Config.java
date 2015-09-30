package edu.sdsu.its.rohan_search;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;


/**
 * Import Config Properties.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */
public class Config {
    private String db_host;
    private String db_name;
    private String db_user;
    private String db_port;
    private String db_password;
    private String email_host;
    private String email_username;
    private String email_password;
    private Integer email_port;
    private Boolean email_ssl;
    private String email_from_name;
    private String email_from_email;

    /**
     * Load the configuration file and set the member variables used in the getters.
     */
    public Config() {
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            final String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
//                Logger.getLogger(getClass()).info("Config File Loaded Successfully ");
            } else {
                Logger.getLogger(getClass()).fatal("Error Could not find Properties File");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass()).error("Error Loading Properties File", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Logger.getLogger(getClass()).warn("Error Closing Properties File", e);
            }
        }

        db_host = prop.getProperty("db_host");
        db_name = prop.getProperty("db_name");
        db_user = prop.getProperty("db_user");
        db_port = prop.getProperty("db_port");
        db_password = prop.getProperty("db_password");
        email_host = prop.getProperty("email_host");
        email_username = prop.getProperty("email_username");
        email_password = prop.getProperty("email_password");
        email_port = Integer.parseInt(prop.getProperty("email_port"));
        email_ssl  = Boolean.parseBoolean(prop.getProperty("email_ssl"));
        email_from_name = prop.getProperty("email_from_name");
        email_from_email = prop.getProperty("email_from_email");
    }

    public static void main(String[] args) {
        new Config();
        System.out.println(new Config().getDb_port());
    }

    public String getDb_host() {
        return db_host;
    }

    public String getDb_name() {
        return db_name;
    }

    public String getDb_user() {
        return db_user;
    }

    public String getDb_port() {
        return db_port;
    }

    public String getDb_password() {
        return db_password;
    }

    public String getEmail_host() {
        return email_host;
    }

    public String getEmail_username() {
        return email_username;
    }

    public String getEmail_password() {
        return email_password;
    }

    public Integer getEmail_port() {
        return email_port;
    }

    public Boolean getEmail_ssl() {
        return email_ssl;
    }

    public String getEmail_from_name() {
        return email_from_name;
    }

    public String getEmail_from_email() {
        return email_from_email;
    }
}
