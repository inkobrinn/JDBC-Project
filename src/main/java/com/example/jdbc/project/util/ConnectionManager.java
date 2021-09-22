package com.example.jdbc.project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String URL = "url";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    static {
        loadDriver();
    }

    private ConnectionManager() {

    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.getProperty(URL),
                    PropertiesUtil.getProperty(LOGIN),
                    PropertiesUtil.getProperty(PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
