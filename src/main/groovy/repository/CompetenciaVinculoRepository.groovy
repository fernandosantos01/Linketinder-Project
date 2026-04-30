package repository

import java.sql.Connection

interface CompetenciaVinculoRepository {
    List<String> buscarCompetenciasDe(
            Integer entidadeId,
            String tabelaJuncao,
            String colunaEntidade,
            String colunaCompetencia,
            Connection conexao
    )

    void vincularCompetencias(
            int entidadeId,
            List<String> competencias,
            String tabelaJuncao,
            String colunaEntidade,
            String colunaCompetencia,
            Connection conexao
    )

    int buscarOuCriarCompetencia(
            String nomeCompetencia,
            Connection conexao
    )

}