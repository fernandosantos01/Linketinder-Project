package dao

import domain.Candidato
import groovy.sql.Sql
import util.DatabaseConnection

class CandidatoDAO {
    static List<Candidato> listarCandidatos() {
        def lista = []
        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return lista
            sql.eachRow("SELECT * FROM candidatos") { row ->
                def candidato = new Candidato(
                        id: row.id,
                        nome: row.nome,
                        dataNascimento: row.data_nascimento?.toLocalDate(),
                        email: row.email,
                        cpf: row.cpf,
                        pais: row.pais,
                        estado: row.estado,
                        cep: row.cep
                )
                candidato.habilidades = buscarHabilidades(row.id, sql)
                lista << candidato
            }
        }
        return lista
    }

    private static List<String> buscarHabilidades(Integer candidatosId, Sql sql) {
        def habilidades = []
        String query = """
            SELECT c.nome
            FROM competencias c
            JOIN candidato_competencias cc ON c.id = cc.competencias_id
            WHERE cc.candidatos_id = ?
        """
        sql.eachRow(query, [candidatosId]) { row ->
            habilidades << row.nome
        }
        return habilidades
    }

    static void salvarCandidato(Candidato c) {

        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return
            sql.execute("""
                INSERT INTO candidatos (nome,data_nascimento, email, cpf, pais, estado, cep, descricao)
                VALUES (${c.nome},${c.dataNascimento}, ${c.email}, ${c.cpf}, ${c.pais}, ${c.estado}, ${c.cep}, ${c.descricao})
            """)
        } catch (Exception e) {
            print("Erro ao salvar o candidato: ${e.message}")
        }
    }
}
