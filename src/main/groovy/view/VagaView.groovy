package view

import domain.Empresa
import domain.Vaga

class VagaView {
    private final Scanner leitor

    VagaView(Scanner leitor) {
        this.leitor = leitor
    }

    void exibirVagas(List<Vaga> vagas) {
        println "\n--- Vagas Disponíveis ---"
        if (vagas.isEmpty()) {
            println "Nenhuma vaga publicada."
            return
        }
        vagas.each { vaga ->
            println "Vaga: ${vaga.nome} | Local: ${vaga.local} | Requisitos: ${vaga.competencias.join(', ')}"
        }
    }

    Vaga lerVaga(List<Empresa> empresas) {
        println "\n--- Publicar Vaga ---"

        if (empresas.isEmpty()) {
            println "Cadastre uma empresa primeiro!"
            return null
        }

        empresas.each { empresa -> println "${empresa.id}: ${empresa.nome}" }

        print "ID da Empresa: "
        int idEmpresa
        try {
            idEmpresa = leitor.nextInt()
            leitor.nextLine()
        } catch (Exception ignored) {
            println "ID de empresa inválido."
            leitor.nextLine()
            return null
        }

        Vaga vaga = new Vaga(empresaId: idEmpresa)

        print "Título da Vaga: "
        vaga.nome = leitor.nextLine()

        print "Regime de Trabalho: "
        vaga.local = leitor.nextLine()

        print "Descrição: "
        vaga.descricao = leitor.nextLine()

        print "Competências Necessárias (vírgula): "
        vaga.competencias = leitor.nextLine().split(",").collect { it.trim() }

        return vaga
    }

    void exibirSucesso(String nome) {
        println "Vaga '${nome}' publicada com sucesso!"
    }

    void exibirErro(String mensagem) {
        println "Ainda não foi possível salvar a vaga: ${mensagem}"
    }
}
