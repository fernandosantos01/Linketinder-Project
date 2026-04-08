package dao

import domain.Vaga
import groovy.sql.Sql
import util.DatabaseConnection

class VagaDAO {
    static List<Vaga> listarVagas() {
        def lista = []
        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return lista
            sql.eachRow("SELECT * FROM vagas") { row ->
                def vaga = new Vaga(
                        id: row.id,
                        nome: row.nome,
                        descricao: row.descricao,
                        local: row.local,
                        empresaId: row.empresa_id
                )
                vaga.competencias = buscarCompetenciasDaVaga(row.id, sql)
                lista << vaga
            }

        } catch (Exception e) {
            println "Erro ao listar vagas: ${e.message}"
        }
        return lista
    }

    static private List<String> buscarCompetenciasDaVaga(Integer vagaId, Sql sql) {
        def comps = []
        String query = """
        SELECT c.nome
        FROM competencias c
        JOIN vaga_competencias vc ON c.id = vc.competencia_id
        WHERE vc.vaga_id = ?
"""
        sql.eachRow(query, [vagaId]) { row ->
            comps << row.nome
        }
        return comps
    }

    static void salvarVaga(Vaga vaga) {
        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return
            def chaves = sql.executeInsert("""
                INSERT INTO vagas (nome, descricao, competencias, local, empresa_id)
                VALUES (${vaga.nome}, ${vaga.descricao}, '', ${vaga.local}, ${vaga.empresaId})
            """)
            def vagaId = chaves[0][0]

            if (vaga.competencias && !vaga.competencias.isEmpty()) {
                vaga.competencias.each { nomeComp ->
                    def compRow = sql.firstRow("SELECT id FROM competencias WHERE nome = ?", [nomeComp])

                    if (compRow) {
                        sql.execute("""
                            INSERT INTO vaga_competencias (vaga_id, competencia_id)
                            VALUES (?, ?)
                        """, [vagaId, compRow.id])
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
