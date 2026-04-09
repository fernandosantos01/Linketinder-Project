package util

import java.sql.Connection
import java.sql.DriverManager

class DataBaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/db_linketinder"
    private static final String USER = "geek"
    private static final String PASSWORD = "university"

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (Exception e) {
            println "Erro ao Criar conexão com o banco: ${e.message}"
            return null
        }
    }
}
