package view

import domain.Candidato

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CandidatoView {
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private final Scanner leitor

    CandidatoView(Scanner leitor) {
        this.leitor = leitor
    }

    void exibirCandidatos(List<Candidato> candidatos) {
        println "\n--- Candidatos ---"
        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado."
            return
        }
        candidatos.each { candidato ->
            println "ID: ${candidato.id} | ${candidato.nome} | Competências: ${candidato.habilidades.join(', ')}"
        }
    }

    Candidato lerCandidato() {
        println "\n--- Novo Candidato ---"
        Candidato candidato = new Candidato()

        print "Nome: "
        candidato.nome = leitor.nextLine()

        print "CPF: "
        candidato.cpf = leitor.nextLine()

        print "Data Nasc (dd/mm/aaaa): "
        try {
            candidato.dataNascimento = LocalDate.parse(leitor.nextLine(), DATA_FORMATTER)
        } catch (Exception ignored) {
            println "Data inválida. Usando data padrão."
            candidato.dataNascimento = LocalDate.now()
        }

        print "Email: "
        candidato.email = leitor.nextLine()

        print "Estado (UF): "
        candidato.estado = leitor.nextLine()

        print "País (BRA): "
        candidato.pais = leitor.nextLine()

        print "CEP (00000-000): "
        candidato.cep = leitor.nextLine()

        print "Descrição: "
        candidato.descricao = leitor.nextLine()

        print "Habilidades (separadas por vírgula): "
        candidato.habilidades = leitor.nextLine().split(",").collect { it.trim() }

        return candidato
    }

    void exibirSucesso(String nome) {
        println "Candidato '${nome}' cadastrado com sucesso!"
    }

    void exibirErro(String mensagem) {
        println "Ainda não foi possível salvar o candidato: ${mensagem}"
    }
}
