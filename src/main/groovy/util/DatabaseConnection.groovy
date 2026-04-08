package util

import groovy.sql.Sql

class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/db_linketinder"
    private static final String USER = "geek"
    private static final String PASSWORD = "university"
    private static final String DRIVER = "org.postgresql.Driver"

    static Sql getConnection() {
        try {
            return Sql.newInstance(URL, USER, PASSWORD, DRIVER)
        } catch (Exception e) {
            println "Erro ao criar conexão com o banco: ${e.message}"
            return null
        }
    }
}