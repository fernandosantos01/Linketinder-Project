package dao

import domain.Vaga
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class VagaDAO {
    private static final String TABELA_JUNCAO = "vaga_competencias"
    private static final String COLUNA_ENTIDADE = "vaga_id"
    private static final String COLUNA_COMPETENCIA = "competencia_id"

    static List<Vaga> listarVagas() {
        String query = "SELECT * FROM vagas"
        List<Vaga> vagas = []

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Vaga vaga = construirVagaDoResultSet(resultSet)
                vaga.competencias = CompetenciaDAO.buscarCompetenciasDe(vaga.id, TABELA_JUNCAO, COLUNA_ENTIDADE, COLUNA_COMPETENCIA, conexao)
                vagas << vaga
            }
        } catch (Exception erro) {
            throw new RuntimeException("Erro ao listar vagas: ${erro.message}")
        }
        return vagas
    }

    static void salvarVaga(Vaga vaga) {
        validarDadosDaVaga(vaga)

        String query = "INSERT INTO vagas (nome, descricao, local, empresa_id) VALUES (?, ?, ?, ?)"

        try (Connection conexao = DataBaseConnection.getConnection()) {
            int novoIdVaga = inserirVaga(query, vaga, conexao)

            if (novoIdVaga > 0 && vaga.competencias) {
                CompetenciaDAO.vincularCompetencias(novoIdVaga, vaga.competencias, TABELA_JUNCAO, COLUNA_ENTIDADE, COLUNA_COMPETENCIA, conexao)
            }

        } catch (Exception erro) {
            throw new RuntimeException("Erro ao salvar vaga: ${erro.message}")
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

    private static void validarDadosDaVaga(Vaga vaga) {
        if (!vaga.nome?.trim()) {
            throw new IllegalArgumentException("Nome da vaga é obrigatório")
        }
        if (!vaga.empresaId || vaga.empresaId <= 0) {
            throw new IllegalArgumentException("Vaga '${vaga.nome}' precisa de um ID de empresa válido")
        }
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