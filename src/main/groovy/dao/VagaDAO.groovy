package dao

import domain.Vaga
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class VagaDAO {

    static List<Vaga> listarVagas() {
        String query = "SELECT * FROM vagas"
        List<Vaga> vagas = []

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Vaga vaga = construirVagaDoResultSet(resultSet)
                vaga.competencias = buscarCompetenciasDaVaga(vaga.id, conexao)
                vagas << vaga
            }
        } catch (Exception erro) {
            println "Erro ao listar vagas: ${erro.message}"
        }
        return vagas
    }

    static void salvarVaga(Vaga vaga) {
        validarDadosDaVaga(vaga)

        String query = "INSERT INTO vagas (nome, descricao, local, empresa_id) VALUES (?, ?, ?, ?)"

        try (Connection conexao = DataBaseConnection.getConnection()) {
            int novoIdVaga = inserirVaga(query, vaga, conexao)

            if (novoIdVaga > 0 && vaga.competencias) {
                vincularCompetenciasAVaga(novoIdVaga, vaga.competencias, conexao)
            }

            println "Vaga '${vaga.nome}' salva com sucesso!"

        } catch (Exception erro) {
            println "Erro ao salvar vaga: ${erro.message}"
        }
    }

    private static int inserirVaga(String query, Vaga vaga, Connection conexao) {
        try (PreparedStatement statement = conexao.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, vaga.nome)
            statement.setString(2, vaga.descricao)
            statement.setString(3, vaga.local)
            statement.setInt(4, vaga.empresaId)

            statement.executeUpdate()

            try (ResultSet chaves = statement.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : -1
            }
        } catch (Exception erro) {
            throw new RuntimeException("Falha ao inserir vaga", erro)
        }
    }

    private static void vincularCompetenciasAVaga(int idVaga, List<String> competencias, Connection conexao) {
        CompetenciaDAO competenciaDAO = new CompetenciaDAO()
        String query = "INSERT INTO vaga_competencias (vaga_id, competencia_id) VALUES (?, ?)"

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            competencias.each { nomeCompetencia ->
                int idCompetencia = competenciaDAO.buscarOuCriarCompetencia(nomeCompetencia, conexao)
                if (idCompetencia > 0) {
                    statement.setInt(1, idVaga)
                    statement.setInt(2, idCompetencia)
                    statement.executeUpdate()
                }
            }
        } catch (Exception erro) {
            println "Erro ao vincular competências à vaga: ${erro.message}"
        }
    }

    private static void validarDadosDaVaga(Vaga vaga) {
        if (!vaga.nome?.trim()) {
            throw new IllegalArgumentException("Nome da vaga é obrigatório")
        }
        if (!vaga.empresaId || vaga.empresaId <= 0) {
            throw new IllegalArgumentException("Vaga '${vaga.nome}' precisa de um ID de empresa válido")
        }
    }

    private static List<String> buscarCompetenciasDaVaga(Integer idVaga, Connection conexao) {
        String query = """
            SELECT c.nome 
            FROM competencias c
            JOIN vaga_competencias vc ON c.id = vc.competencia_id
            WHERE vc.vaga_id = ?
        """
        List<String> competencias = []

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            statement.setInt(1, idVaga)
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    competencias << resultSet.getString("nome")
                }
            }
        } catch (Exception erro) {
            println "Erro ao buscar competências da vaga: ${erro.message}"
        }
        return competencias
    }

    private static Vaga construirVagaDoResultSet(ResultSet resultSet) {
        return new Vaga(
            id: resultSet.getInt("id"),
            nome: resultSet.getString("nome"),
            descricao: resultSet.getString("descricao"),
            local: resultSet.getString("local"),
            empresaId: resultSet.getInt("empresa_id")
        )
    }
}