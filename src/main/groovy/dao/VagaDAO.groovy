package dao

import domain.Vaga
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class VagaDAO {

    static List<Vaga> listarVagas() {
        def lista = []
        String query = "SELECT * FROM vagas"

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                def vaga = new Vaga(
                        id: rs.getInt("id"),
                        nome: rs.getString("nome"),
                        descricao: rs.getString("descricao"),
                        local: rs.getString("local"),
                        empresaId: rs.getInt("empresa_id")
                )
                vaga.competencias = buscarCompetenciasDaVaga(vaga.id, conn)
                lista << vaga
            }
        } catch (Exception e) {
            println "Erro ao listar vagas: ${e.message}"
        }
        return lista
    }

    private static List<String> buscarCompetenciasDaVaga(Integer vagaId, Connection conn) {
        def comps = []
        String query = """
            SELECT c.nome 
            FROM competencias c
            JOIN vaga_competencias vc ON c.id = vc.competencia_id
            WHERE vc.vaga_id = ?
        """

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vagaId)
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comps << rs.getString("nome")
                }
            }
        } catch (Exception e) {
            println "Erro ao buscar competências: ${e.message}"
        }
        return comps
    }

    static void salvarVaga(Vaga vaga) {
        if (!vaga.empresaId) {
            println "Erro: A vaga '${vaga.nome}' precisa de um ID de empresa válido para ser salva."
            return
        }
        String queryVaga = "INSERT INTO vagas (nome, descricao, local, empresa_id) VALUES (?, ?, ?, ?)"

        try (Connection conn = DataBaseConnection.getConnection()) {

            PreparedStatement stmtVaga = conn.prepareStatement(queryVaga, Statement.RETURN_GENERATED_KEYS)
            stmtVaga.setString(1, vaga.nome)
            stmtVaga.setString(2, vaga.descricao)
            stmtVaga.setString(3, vaga.local)
            stmtVaga.setInt(4, vaga.empresaId)

            stmtVaga.executeUpdate()

            ResultSet rsKeys = stmtVaga.getGeneratedKeys()
            int vagaIdGerado = 0
            if (rsKeys.next()) {
                vagaIdGerado = rsKeys.getInt(1)
            }
            stmtVaga.close()

            if (vagaIdGerado > 0 && vaga.competencias != null) {
                def compDAO = new CompetenciaDAO()
                String queryInsertVagaComp = "INSERT INTO vaga_competencias (vaga_id, competencia_id) VALUES (?, ?)"

                try (PreparedStatement stmtInsertVagaComp = conn.prepareStatement(queryInsertVagaComp)) {
                    vaga.competencias.each { nomeComp ->

                        int compId = compDAO.buscarOuCriarCompetencia(nomeComp, conn)

                        if (compId > 0) {
                            stmtInsertVagaComp.setInt(1, vagaIdGerado)
                            stmtInsertVagaComp.setInt(2, compId)
                            stmtInsertVagaComp.executeUpdate()
                        }
                    }
                }
            }
            println "Vaga '${vaga.nome}' salva com sucesso!"

        } catch (Exception e) {
            println "Erro ao salvar vaga: ${e.message}"
            e.printStackTrace()
        }
    }
}