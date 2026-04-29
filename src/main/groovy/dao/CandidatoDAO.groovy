package dao

import domain.Candidato
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Date

class CandidatoDAO {
    private static final String TABELA_JUNCAO = "candidato_competencias"
    private static final String COLUNA_ENTIDADE = "candidato_id"
    private static final String COLUNA_COMPETENCIA = "competencias_id"

    static List<Candidato> listarCandidatos() {
        String query = "SELECT * FROM candidatos"
        List<Candidato> candidatos = []

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Candidato candidato = construirCandidatoDoResultSet(resultSet)
                candidato.habilidades = CompetenciaDAO.buscarCompetenciasDe(candidato.id, TABELA_JUNCAO, COLUNA_ENTIDADE, COLUNA_COMPETENCIA, conexao)
                candidatos << candidato
            }
        } catch (Exception erro) {
            println "Erro ao listar candidatos: ${erro.message}"
        }
        return candidatos
    }

    static void salvarCandidato(Candidato candidato) {
        validarDadosDoCandidato(candidato)

        String query = """
            INSERT INTO candidatos (nome, data_nascimento, email, cpf, pais, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conexao = DataBaseConnection.getConnection()) {
            int novoIdCandidato = inserirCandidato(query, candidato, conexao)

            if (novoIdCandidato > 0 && candidato.habilidades) {
                CompetenciaDAO.vincularCompetencias(novoIdCandidato, candidato.habilidades, TABELA_JUNCAO, COLUNA_ENTIDADE, COLUNA_COMPETENCIA, conexao)
            }

            println "Candidato '${candidato.nome}' salvo com sucesso!"

        } catch (Exception erro) {
            println "Erro ao salvar candidato: ${erro.message}"
        }
    }

    private static int inserirCandidato(String query, Candidato candidato, Connection conexao) {
        try (PreparedStatement statement = conexao.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, candidato.nome)
            statement.setDate(2, Date.valueOf(candidato.dataNascimento))
            statement.setString(3, candidato.email)
            statement.setString(4, candidato.cpf)
            statement.setString(5, candidato.pais)
            statement.setString(6, candidato.estado)
            statement.setString(7, candidato.cep)
            statement.setString(8, candidato.descricao)

            statement.executeUpdate()

            try (ResultSet chaves = statement.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : -1
            }
        } catch (Exception erro) {
            throw new RuntimeException("Falha ao inserir candidato", erro)
        }
    }
    private static void validarDadosDoCandidato(Candidato candidato) {
        if (!candidato.nome?.trim()) {
            throw new IllegalArgumentException("Nome do candidato é obrigatório")
        }
        if (!candidato.cpf?.trim()) {
            throw new IllegalArgumentException("CPF é obrigatório")
        }
        if (!candidato.dataNascimento) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória")
        }
    }
    private static Candidato construirCandidatoDoResultSet(ResultSet resultSet) {
        return new Candidato(
                id: resultSet.getInt("id"),
                nome: resultSet.getString("nome"),
                dataNascimento: resultSet.getDate("data_nascimento")?.toLocalDate(),
                email: resultSet.getString("email"),
                cpf: resultSet.getString("cpf"),
                pais: resultSet.getString("pais"),
                estado: resultSet.getString("estado"),
                cep: resultSet.getString("cep"),
                descricao: resultSet.getString("descricao")
        )
    }
}