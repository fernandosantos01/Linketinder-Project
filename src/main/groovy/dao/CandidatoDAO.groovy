package dao

import domain.Candidato
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Date

class CandidatoDAO {
    static List<Candidato> listarCandidatos() {
        String query = "SELECT * FROM candidatos"
        List<Candidato> candidatos = []

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Candidato candidato = construirCandidatoDoResultSet(resultSet)
                candidato.habilidades = buscarHabilidadesDocandidato(candidato.id, conexao)
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
                vincularHabilidades(novoIdCandidato, candidato.habilidades, conexao)
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

    private static void vincularHabilidades(int idCandidato, List<String> habilidades, Connection conexao) {
        CompetenciaDAO competenciaDAO = new CompetenciaDAO()
        String query = "INSERT INTO candidato_competencias (candidato_id, competencias_id) VALUES (?, ?)"

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            habilidades.each { nomeHabilidade ->
                int idCompetencia = competenciaDAO.buscarOuCriarCompetencia(nomeHabilidade, conexao)
                if (idCompetencia > 0) {
                    statement.setInt(1, idCandidato)
                    statement.setInt(2, idCompetencia)
                    statement.executeUpdate()
                }
            }
        } catch (Exception erro) {
            println "Erro ao vincular habilidades: ${erro.message}"
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

    private static List<String> buscarHabilidadesDocandidato(Integer idCandidato, Connection conexao) {
        String query = """
            SELECT c.nome 
            FROM competencias c
            JOIN candidato_competencias cc ON c.id = cc.competencias_id
            WHERE cc.candidato_id = ?
        """
        List<String> habilidades = []

        try (PreparedStatement statement = conexao.prepareStatement(query)) {
            statement.setInt(1, idCandidato)
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habilidades << resultSet.getString("nome")
                }
            }
        } catch (Exception erro) {
            println "Erro ao buscar habilidades do candidato: ${erro.message}"
        }
        return habilidades
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