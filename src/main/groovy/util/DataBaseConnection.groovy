package util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DataBaseConnection {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/db_linketinder"
    private static final String DATABASE_USER = "geek"
    private static final String DATABASE_PASSWORD = "university"

    static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)
        } catch (SQLException sqlException) {
            throw new RuntimeException("Falha ao conectar ao banco de dados: ${sqlException.message}", sqlException)
        }
    }

    static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close()
            } catch (SQLException ignored) {}
        }
    }
}
