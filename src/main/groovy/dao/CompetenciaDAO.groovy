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
        String nomeNormalizado = normalizarNomeCompetencia(nomeCompetencia)
        int id = buscarCompetencia(nomeNormalizado, conn)
        return id > 0 ? id : criarCompetencia(nomeCompetencia, conn)
    }

    private static int buscarCompetencia(String nomeCompetencia, Connection conn) {
        String query = "SELECT id FROM competencias WHERE nome = ?"

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nomeCompetencia)
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar competência '${nomeCompetencia}'", e)
        }
    }

    private static int criarCompetencia(String nomeCompetencia, Connection conn) {
        String query = """
        INSERT INTO competencias (nome) VALUES (?)
        """
        try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nomeCompetencia)
            statement.executeUpdate()
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1)
                }
            }
        } catch (Exception erro) {
            throw new RuntimeException("Erro ao criar competencia '${nomeCompetencia}'", erro)
        }
        return -1
    }

    static List<String> buscarCompetenciasDe(
            Integer entidadeId,
            String tabelaJuncao,
            String colunaEntidade,
            String colunaCompetencia,
            Connection conn
    ) {
        String query = """
            SELECT c.nome
            FROM competencias c
            JOIN ${tabelaJuncao} j
              ON c.id = j.${colunaCompetencia}
            WHERE j.${colunaEntidade} = ?
        """

        List<String> competencias = []

        try (PreparedStatement smt = conn.prepareStatement(query)) {
            smt.setInt(1, entidadeId)
            try (ResultSet rs = smt.executeQuery()) {
                while (rs.next()) {
                    competencias << rs.getString("nome")
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar competências para entidade ID ${entidadeId} na tabela ${tabelaJuncao}", e)
        }
        return competencias
    }

    static void vincularCompetencias(
            int entidadeId,
            List<String> competencias,
            String tabelaJuncao,
            String colunaEntidade,
            String colunaCompetencia,
            Connection conn
    ) {
        String query = """
        INSERT INTO ${tabelaJuncao}
        (${colunaEntidade}, ${colunaCompetencia})
        VALUES (?, ?)
        """
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            competencias.forEach { nomeCompetencia ->
                int idCompetencia = buscarOuCriarCompetencia(nomeCompetencia, conn)
                if (idCompetencia > 0) {
                    statement.setInt(1, entidadeId)
                    statement.setInt(2, idCompetencia)
                    statement.executeUpdate()
                }
            }
        } catch (Exception erro) {
            throw new RuntimeException("Erro ao vincular competências para entidade ID ${entidadeId} na tabela ${tabelaJuncao}", erro)
        }
    }

    private static String normalizarNomeCompetencia(String nome) {
        return nome?.trim()?.toUpperCase() ?: ""
    }
}