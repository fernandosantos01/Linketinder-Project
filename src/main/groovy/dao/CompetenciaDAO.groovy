package dao

import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class CompetenciaDAO {
    static List<String> listarCompetencias() {
        def lista = []
        String query = "SELECT nome FROM competencias ORDER BY nome ASC"

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista << rs.getString("nome")
            }
        } catch (Exception e) {
            println "Erro ao listar competências: ${e.message}"
        }
        return lista
    }

    static int buscarOuCriarCompetencia(String nomeCompetencia, Connection conn) {
        int id = -1

        String queryBusca = "SELECT id FROM competencias WHERE nome = ?"
        try (PreparedStatement stmtBusca = conn.prepareStatement(queryBusca)) {
            stmtBusca.setString(1, nomeCompetencia)
            ResultSet rs = stmtBusca.executeQuery()

            if (rs.next()) {
                id = rs.getInt("id")
            }
            rs.close()
        } catch (Exception e) {
            println "Erro ao buscar competência '${nomeCompetencia}': ${e.message}"
        }
        if (id > 0) {
            return id
        }

        String queryInsert = "INSERT INTO competencias (nome) VALUES (?)"
        try (PreparedStatement stmtInsert = conn.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            stmtInsert.setString(1, nomeCompetencia)
            stmtInsert.executeUpdate()

            ResultSet rsKeys = stmtInsert.getGeneratedKeys()
            if (rsKeys.next()) {
                id = rsKeys.getInt(1)
                println "Nova competência '${nomeCompetencia}' cadastrada no sistema!"
            }
            rsKeys.close()
        } catch (Exception e) {
            println "Erro ao criar nova competência '${nomeCompetencia}': ${e.message}"
        }

        return id
    }
}