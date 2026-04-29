package dao

import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class CompetenciaDAO {
    static List<String> listarCompetencias() {
        List<String> lista = []
        String query = "SELECT nome FROM competencias ORDER BY nome ASC"

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                lista << resultSet.getString("nome")
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar competências: ${e.message}")
        }
        return lista
    }

    static int buscarOuCriarCompetencia(String nomeCompetencia, Connection conexao) {
        String nomeNormalizado = normalizarNomeCompetencia(nomeCompetencia)
        int id = buscarCompetencia(nomeNormalizado, conexao)
        return id > 0 ? id : criarCompetencia(nomeNormalizado, conexao)
    }

    private static int buscarCompetencia(String nomeCompetencia, Connection conexao) {
        String query = "SELECT id FROM competencias WHERE nome = ?"

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            statement.setString(1, nomeCompetencia)
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : -1
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar competência '${nomeCompetencia}'", e)
        }
    }

    private static int criarCompetencia(String nomeCompetencia, Connection conexao) {
        String query = """
        INSERT INTO competencias (nome) VALUES (?)
        """
        try (PreparedStatement statement = conexao.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nomeCompetencia)
            statement.executeUpdate()
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1)
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
            Connection conexao
    ) {
        String query = """
            SELECT c.nome
            FROM competencias c
            JOIN ${tabelaJuncao} j
              ON c.id = j.${colunaCompetencia}
            WHERE j.${colunaEntidade} = ?
        """

        List<String> competencias = []

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            statement.setInt(1, entidadeId)
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    competencias << resultSet.getString("nome")
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
            Connection conexao
    ) {
        String query = """
        INSERT INTO ${tabelaJuncao}
        (${colunaEntidade}, ${colunaCompetencia})
        VALUES (?, ?)
        """
        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            competencias.forEach { nomeCompetencia ->
                int idCompetencia = buscarOuCriarCompetencia(nomeCompetencia, conexao)
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