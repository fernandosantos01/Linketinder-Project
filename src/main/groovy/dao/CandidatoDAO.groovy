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
        def lista = []
        String query = "SELECT * FROM candidatos"

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                def candidato = new Candidato(
                        id: rs.getInt("id"),
                        nome: rs.getString("nome"),
                        dataNascimento: rs.getDate("data_nascimento")?.toLocalDate(),
                        email: rs.getString("email"),
                        cpf: rs.getString("cpf"),
                        pais: rs.getString("pais"),
                        estado: rs.getString("estado"),
                        cep: rs.getString("cep"),
                        descricao: rs.getString("descricao")
                )
                candidato.habilidades = buscarHabilidades(candidato.id, conn)
                lista << candidato
            }
        } catch (Exception e) {
            println "Erro ao listar candidatos: ${e.message}"
        }
        return lista
    }

    private static List<String> buscarHabilidades(Integer candidatoId, Connection conn) {
        def habs = []
        String query = """
            SELECT c.nome 
            FROM competencias c
            JOIN candidato_competencias cc ON c.id = cc.competencias_id
            WHERE cc.candidato_id = ?
        """

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, candidatoId)
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    habs << rs.getString("nome")
                }
            }
        } catch (Exception e) {
            println "Erro ao buscar habilidades do candidato: ${e.message}"
        }
        return habs
    }

    static void salvarCandidato(Candidato c) {
        String queryCandidato = """
            INSERT INTO candidatos (nome, data_nascimento, email, cpf, pais, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conn = DataBaseConnection.getConnection()) {

            PreparedStatement stmtCand = conn.prepareStatement(queryCandidato, Statement.RETURN_GENERATED_KEYS)
            stmtCand.setString(1, c.nome)
            stmtCand.setDate(2, Date.valueOf(c.dataNascimento))
            stmtCand.setString(3, c.email)
            stmtCand.setString(4, c.cpf)
            stmtCand.setString(5, c.pais)
            stmtCand.setString(6, c.estado)
            stmtCand.setString(7, c.cep)
            stmtCand.setString(8, c.descricao)

            stmtCand.executeUpdate()

            ResultSet rsKeys = stmtCand.getGeneratedKeys()
            int candidatoIdGerado = 0
            if (rsKeys.next()) {
                candidatoIdGerado = rsKeys.getInt(1)
            }
            stmtCand.close()

            if (candidatoIdGerado > 0 && c.habilidades != null && !c.habilidades.isEmpty()) {

                def compDAO = new CompetenciaDAO()

                String queryInsertCandComp = "INSERT INTO candidato_competencias (candidato_id, competencias_id) VALUES (?, ?)"

                try (PreparedStatement stmtInsert = conn.prepareStatement(queryInsertCandComp)) {
                    c.habilidades.each { nomeHab ->

                        int compId = compDAO.buscarOuCriarCompetencia(nomeHab, conn)

                        if (compId > 0) {
                            stmtInsert.setInt(1, candidatoIdGerado)
                            stmtInsert.setInt(2, compId)
                            stmtInsert.executeUpdate()
                        }
                    }
                }
            }

            println "Candidato '${c.nome}' salvo com sucesso!"

        } catch (Exception e) {
            println "Erro ao salvar candidato: ${e.message}"
            e.printStackTrace()
        }
    }
}