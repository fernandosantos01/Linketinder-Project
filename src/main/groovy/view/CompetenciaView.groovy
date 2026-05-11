package view

class CompetenciaView {

    void exibirCompetencias(List<String> competencias) {
        println "\n--- Dicionário de Competências ---"
        if (competencias.isEmpty()) {
            println "Nenhuma competência cadastrada."
            return
        }
        competencias.each { competencia -> println "- ${competencia}" }
    }
}
