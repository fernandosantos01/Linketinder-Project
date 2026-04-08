package domain

import groovy.transform.Canonical

@Canonical
class Vaga {
    Integer id
    String nome
    String descricao
    String local
    Integer empresaId
    List<String> competencias = []

    @Override
    String toString() {
        return """
        ------------------------------------------
        VAGA: $nome
        Local: ${local ?: 'Remoto/Não informado'}
        Descrição: $descricao
        Competências Necessárias: ${competencias.join(', ')}
        ------------------------------------------"""
    }
}
