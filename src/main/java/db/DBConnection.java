package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // Параметры подключения
    private final String URL = "jdbc:postgresql://34.118.52.174:5432/naval";
    private final String USER = "rauan";
    private final String PASSWORD = "0000";

    private DBConnection() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            throw new SQLException("Ошибка подключения к базе данных PostgreSQL", ex);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            try {
                instance = new DBConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
