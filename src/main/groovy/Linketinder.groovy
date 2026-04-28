import dao.*
import domain.Candidato
import domain.Empresa
import domain.Vaga

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Linketinder {
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private static final int OPCAO_SAIR = 0

    private static EmpresaDAO empresaDAO = new EmpresaDAO()
    private static CandidatoDAO candidatoDAO = new CandidatoDAO()
    private static VagaDAO vagaDAO = new VagaDAO()
    private static CompetenciaDAO competenciaDAO = new CompetenciaDAO()
    private static Scanner leitor = new Scanner(System.in)

    static void main(String[] args) {
        exibirMenuPrincipal()
    }

    private static void exibirMenuPrincipal() {
        int opcaoSelecionada = -1

        while (opcaoSelecionada != OPCAO_SAIR) {
            exibirOpcoes()
            opcaoSelecionada = obterOpcaoDoUsuario()
            executarOpcaoSelecionada(opcaoSelecionada)
        }

        println "Finalizando..."
    }

    private static void exibirOpcoes() {
        println "\n==== LINKETINDER - DATABASE EDITION ===="
        println "1. Listar Empresas"
        println "2. Listar Candidatos"
        println "3. Cadastrar Empresa"
        println "4. Cadastrar Candidato"
        println "5. Publicar Vaga"
        println "6. Listar Vagas"
        println "7. Ver Competências do Sistema"
        println "0. Sair"
        print "Escolha: "
    }

    private static int obterOpcaoDoUsuario() {
        try {
            int opcao = leitor.nextInt()
            leitor.nextLine()
            return opcao
        } catch (Exception erro) {
            println "Por favor, digite um número válido."
            leitor.nextLine()
            return -1
        }
    }

    private static void executarOpcaoSelecionada(int opcao) {
        switch (opcao) {
            case 1:
                listarEmpresas()
                break
            case 2:
                listarCandidatos()
                break
            case 3:
                cadastrarEmpresa()
                break
            case 4:
                cadastrarCandidato()
                break
            case 5:
                publicarVaga()
                break
            case 6:
                listarVagas()
                break
            case 7:
                exibirCompetencias()
                break
            case OPCAO_SAIR:
                break
            default:
                println "Opção inválida!"
        }
    }

    private static void listarEmpresas() {
        println "\n--- Empresas ---"
        def empresas = empresaDAO.listarEmpresas()
        if (empresas.isEmpty()) {
            println "Nenhuma empresa cadastrada."
            return
        }
        empresas.each { empresa ->
            println "ID: ${empresa.id} | ${empresa.nome} (${empresa.cnpj})"
        }
    }

    private static void listarCandidatos() {
        println "\n--- Candidatos ---"
        def candidatos = candidatoDAO.listarCandidatos()
        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado."
            return
        }
        candidatos.each { candidato ->
            println "ID: ${candidato.id} | ${candidato.nome} | Competências: ${candidato.habilidades.join(', ')}"
        }
    }

    private static void cadastrarEmpresa() {
        println "\n--- Nova Empresa ---"
        Empresa novaEmpresa = new Empresa()

        print "Nome: "
        novaEmpresa.nome = leitor.nextLine()

        print "CNPJ: "
        novaEmpresa.cnpj = leitor.nextLine()

        print "Email: "
        novaEmpresa.email = leitor.nextLine()

        print "Descrição: "
        novaEmpresa.descricao = leitor.nextLine()

        print "Estado (UF): "
        novaEmpresa.estado = leitor.nextLine()

        try {
            empresaDAO.salvarEmpresa(novaEmpresa)
        } catch (Exception erro) {
            println "Ainda não foi possível salvar a empresa: ${erro.message}"
        }
    }

    private static void cadastrarCandidato() {
        println "\n--- Novo Candidato ---"
        Candidato novoCandidato = new Candidato()

        print "Nome: "
        novoCandidato.nome = leitor.nextLine()

        print "CPF: "
        novoCandidato.cpf = leitor.nextLine()

        print "Data Nasc (dd/mm/aaaa): "
        try {
            novoCandidato.dataNascimento = LocalDate.parse(leitor.nextLine(), DATA_FORMATTER)
        } catch (Exception erro) {
            println "Data inválida. Usando data padrão."
            novoCandidato.dataNascimento = LocalDate.now()
        }

        print "Email: "
        novoCandidato.email = leitor.nextLine()

        print "Estado (UF): "
        novoCandidato.estado = leitor.nextLine()

        print "País (BRA): "
        novoCandidato.pais = leitor.nextLine()

        print "CEP (00000-000): "
        novoCandidato.cep = leitor.nextLine()

        print "Descrição: "
        novoCandidato.descricao = leitor.nextLine()

        print "Habilidades (separadas por vírgula): "
        novoCandidato.habilidades = leitor.nextLine().split(",").collect { it.trim() }

        try {
            candidatoDAO.salvarCandidato(novoCandidato)
        } catch (Exception erro) {
            println "Ainda não foi possível salvar o candidato: ${erro.message}"
        }
    }

    private static void publicarVaga() {
        println "\n--- Publicar Vaga ---"
        def empresas = empresaDAO.listarEmpresas()

        if (empresas.isEmpty()) {
            println "Cadastre uma empresa primeiro!"
            return
        }

        empresas.each { empresa -> println "${empresa.id}: ${empresa.nome}" }

        print "ID da Empresa: "
        int idEmpresa
        try {
            idEmpresa = leitor.nextInt()
            leitor.nextLine()
        } catch (Exception erro) {
            println "ID de empresa inválido."
            leitor.nextLine()
            return
        }

        Vaga novaVaga = new Vaga(empresaId: idEmpresa)

        print "Título da Vaga: "
        novaVaga.nome = leitor.nextLine()

        print "Descrição: "
        novaVaga.descricao = leitor.nextLine()

        print "Competências Necessárias (vírgula): "
        novaVaga.competencias = leitor.nextLine().split(",").collect { it.trim() }

        try {
            vagaDAO.salvarVaga(novaVaga)
        } catch (Exception erro) {
            println "Ainda não foi possível salvar a vaga: ${erro.message}"
        }
    }

    private static void listarVagas() {
        println "\n--- Vagas Disponíveis ---"
        def vagas = vagaDAO.listarVagas()

        if (vagas.isEmpty()) {
            println "Nenhuma vaga publicada."
            return
        }

        vagas.each { vaga ->
            println "Vaga: ${vaga.nome} | Requisitos: ${vaga.competencias.join(', ')}"
        }
    }

    private static void exibirCompetencias() {
        println "\n--- Dicionário de Competências ---"
        def competencias = competenciaDAO.listarCompetencias()

        if (competencias.isEmpty()) {
            println "Nenhuma competência cadastrada."
            return
        }

        competencias.each { competencia -> println "- ${competencia}" }
    }
}
