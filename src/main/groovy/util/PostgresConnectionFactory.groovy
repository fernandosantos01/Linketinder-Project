package util


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class PostgresConnectionFactory implements IConnectionFactory {
    @Override
    Connection createConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/db_linketinder"
            return DriverManager.getConnection(url, "geek", "university")
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o PostgreSQL", e)
        }
    }
}
