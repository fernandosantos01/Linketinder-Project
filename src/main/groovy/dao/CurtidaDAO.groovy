package dao

import domain.Match
import repository.CurtidaRepository
import util.IConnectionFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CurtidaDAO implements CurtidaRepository {
    private final IConnectionFactory connectionFactory

    CurtidaDAO(IConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory
    }

    @Override
    Match candidatoCurteVaga(Integer candidatoId, Integer vagaId) {
        try (Connection conexao = connectionFactory.createConnection()) {
            inserirCurtidaCandidato(candidatoId, vagaId, conexao)
            Integer empresaId = buscarEmpresaQueCurtiuCandidato(candidatoId, vagaId, conexao)
            if (empresaId != null) {
                inserirMatch(candidatoId, empresaId, vagaId, conexao)
                return new Match(candidatoId: candidatoId, empresaId: empresaId, vagaId: vagaId)
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar curtida do candidato: ${e.message}", e)
        }
        return null
    }

    @Override
    Match empresaCurteCandidato(Integer empresaId, Integer candidatoId) {
        try (Connection conexao = connectionFactory.createConnection()) {
            inserirCurtidaEmpresa(empresaId, candidatoId, conexao)
            Integer vagaId = buscarVagaDaCurtidaMutua(candidatoId, empresaId, conexao)
            if (vagaId != null) {
                inserirMatch(candidatoId, empresaId, vagaId, conexao)
                return new Match(candidatoId: candidatoId, empresaId: empresaId, vagaId: vagaId)
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar curtida da empresa: ${e.message}", e)
        }
        return null
    }

    private static void inserirCurtidaCandidato(Integer candidatoId, Integer vagaId, Connection conexao) {
        String query = """
            INSERT INTO curtidas_candidato (candidato_id, vaga_id)
            VALUES (?, ?)
            ON CONFLICT (candidato_id, vaga_id) DO NOTHING
        """
        try (PreparedStatement stmt = conexao.prepareStatement(query)) {
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, vagaId)
            stmt.executeUpdate()
        }
    }

    private static void inserirCurtidaEmpresa(Integer empresaId, Integer candidatoId, Connection conexao) {
        String query = """
            INSERT INTO curtidas_empresa (empresa_id, candidato_id)
            VALUES (?, ?)
            ON CONFLICT (empresa_id, candidato_id) DO NOTHING
        """
        try (PreparedStatement stmt = conexao.prepareStatement(query)) {
            stmt.setInt(1, empresaId)
            stmt.setInt(2, candidatoId)
            stmt.executeUpdate()
        }
    }

    private static Integer buscarVagaDaCurtidaMutua(Integer candidatoId, Integer empresaId, Connection conexao) {
        String query = """
            SELECT cc.vaga_id
            FROM curtidas_candidato cc
            JOIN vagas v ON v.id = cc.vaga_id
            WHERE cc.candidato_id = ?
              AND v.empresa_id    = ?
            LIMIT 1
        """
        try (PreparedStatement stmt = conexao.prepareStatement(query)) {
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, empresaId)
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("vaga_id") : null
            }
        }
    }

    private static Integer buscarEmpresaQueCurtiuCandidato(Integer candidatoId, Integer vagaId, Connection conexao) {
        String query = """
            SELECT v.empresa_id
            FROM vagas v
            JOIN curtidas_empresa ce ON ce.empresa_id = v.empresa_id
            WHERE v.id            = ?
              AND ce.candidato_id = ?
            LIMIT 1
        """
        try (PreparedStatement stmt = conexao.prepareStatement(query)) {
            stmt.setInt(1, vagaId)
            stmt.setInt(2, candidatoId)
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("empresa_id") : null
            }
        }
    }

    private static void inserirMatch(Integer candidatoId, Integer empresaId, Integer vagaId, Connection conexao) {
        String query = """
            INSERT INTO matches (candidato_id, empresa_id, vaga_id)
            VALUES (?, ?, ?)
            ON CONFLICT (candidato_id, empresa_id) DO NOTHING
        """
        try (PreparedStatement stmt = conexao.prepareStatement(query)) {
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, empresaId)
            stmt.setInt(3, vagaId)
            stmt.executeUpdate()
        }
    }
}
