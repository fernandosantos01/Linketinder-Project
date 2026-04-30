import dao.*
import domain.Candidato
import domain.Empresa
import domain.Vaga
import repository.CandidatoRepository
import repository.CompetenciaRepository
import repository.EmpresaRepository
import repository.VagaRepository
import service.CandidatoService
import service.CompetenciaService
import service.EmpresaService
import service.VagaService

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Linketinder {
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private static final int OPCAO_SAIR = 0
    private static Scanner leitor = new Scanner(System.in)

    private static final CompetenciaDAO competenciaDAO = new CompetenciaDAO()
    private static final CandidatoRepository candidatoRepository = new CandidatoDAO(competenciaDAO)
    private static final EmpresaRepository empresaRepository = new EmpresaDAO()
    private static final VagaRepository vagaRepository = new VagaDAO(competenciaDAO)
    private static final CompetenciaRepository competenciaRepository = competenciaDAO

    private static final CandidatoService candidatoService = new CandidatoService(candidatoRepository)
    private static final EmpresaService empresaService = new EmpresaService(empresaRepository)
    private static final VagaService vagaService = new VagaService(vagaRepository)
    private static final CompetenciaService competenciaService = new CompetenciaService(competenciaRepository)


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
        def empresas = empresaService.listarEmpresas()
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
        def candidatos = candidatoService.listarCandidatos()
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
            empresaService.cadastrarEmpresas(novaEmpresa)
            println "Empresa '${novaEmpresa.nome}' cadastrada com sucesso!"
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
            candidatoService.cadastrarCandidato(novoCandidato)
            println "Candidato '${novoCandidato.nome}' cadastrado com sucesso!"
        } catch (Exception erro) {
            println "Ainda não foi possível salvar o candidato: ${erro.message}"
        }
    }

    private static void publicarVaga() {
        println "\n--- Publicar Vaga ---"
        def empresas = empresaService.listarEmpresas()

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

        print "Regime de Trabalho: "
        novaVaga.local = leitor.nextLine()

        print "Descrição: "
        novaVaga.descricao = leitor.nextLine()

        print "Competências Necessárias (vírgula): "
        novaVaga.competencias = leitor.nextLine().split(",").collect { it.trim() }

        try {
            vagaService.salvarVaga(novaVaga)
            println "Vaga '${novaVaga.nome}' publicada com sucesso!"
        } catch (Exception erro) {
            println "Ainda não foi possível salvar a vaga: ${erro.message}"
        }
    }

    private static void listarVagas() {
        println "\n--- Vagas Disponíveis ---"
        def vagas = vagaService.listarVagas()

        if (vagas.isEmpty()) {
            println "Nenhuma vaga publicada."
            return
        }

        vagas.each { vaga ->
            println "Vaga: ${vaga.nome} | Local: ${vaga.local} | Requisitos: ${vaga.competencias.join(', ')}"
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
