package view

import domain.Match

class CurtidaView {
    private final Scanner leitor

    CurtidaView(Scanner leitor) {
        this.leitor = leitor
    }

    List<Integer> lerCurtidaCandidato() {
        print "ID do candidato: "
        Integer candidatoId = lerInt()
        print "ID da vaga: "
        Integer vagaId = lerInt()
        return [candidatoId, vagaId]
    }

    List<Integer> lerCurtidaEmpresa() {
        print "ID da empresa: "
        Integer empresaId = lerInt()
        print "ID do candidato: "
        Integer candidatoId = lerInt()
        return [empresaId, candidatoId]
    }

    void exibirCurtidaRegistrada() {
        println "Curtida registrada com sucesso!"
    }

    void exibirMatch(Match match) {
        println "\n*** MATCH! ***"
        println "Candidato (ID: ${match.candidatoId}) e empresa (ID: ${match.empresaId}) se curtiram!"
        println "Vaga de origem: ID ${match.vagaId}"
        println "Entre em contato para avançar no processo!"
        println "**************"
    }

    void exibirErro(String mensagem) {
        println "Erro: ${mensagem}"
    }

    private Integer lerInt() {
        try {
            int val = leitor.nextInt()
            leitor.nextLine()
            return val
        } catch (Exception ignored) {
            leitor.nextLine()
            return -1
        }
    }
}
