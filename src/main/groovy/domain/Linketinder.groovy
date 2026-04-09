package domain

import dao.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Linketinder {
    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        def empresaDAO = new EmpresaDAO()
        def candidatoDAO = new CandidatoDAO()
        def vagaDAO = new VagaDAO()
        def compDAO = new CompetenciaDAO()

        int opcao = -1

        while (opcao != 0) {
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

            try {
                opcao = scanner.nextInt()
                scanner.nextLine()
            } catch (Exception e) {
                println "Por favor, digite um número válido."
                scanner.nextLine()
                continue
            }

            switch (opcao) {
                case 1:
                    println "\n--- Empresas ---"
                    empresaDAO.listarEmpresas().each { println "ID: ${it.id} | ${it.nome} (${it.cnpj})" }
                    break

                case 2:
                    println "\n--- Candidatos ---"
                    candidatoDAO.listarCandidatos().each {
                        println "ID: ${it.id} | ${it.nome} | Competências: ${it.habilidades.join(', ')}"
                    }
                    break

                case 3:
                    println "\n--- Nova Empresa ---"
                    def e = new Empresa()
                    print "Nome: "; e.nome = scanner.nextLine()
                    print "CNPJ: "; e.cnpj = scanner.nextLine()
                    print "Email: "; e.email = scanner.nextLine()
                    print "Descrição: "; e.descricao = scanner.nextLine()
                    print "Estado (UF): "; e.estado = scanner.nextLine()
                    empresaDAO.salvarEmpresa(e)
                    break

                case 4:
                    println "\n--- Novo Candidato ---"
                    def c = new Candidato()
                    print "Nome: "; c.nome = scanner.nextLine()
                    print "CPF: "; c.cpf = scanner.nextLine()
                    print "Data Nasc (dd/mm/aaaa): "
                    c.dataNascimento = LocalDate.parse(scanner.nextLine(), formatter)
                    print "Email: "; c.email = scanner.nextLine()
                    print "Estado (UF): "; c.estado = scanner.nextLine()
                    print "Pais (BRA): "; c.pais = scanner.nextLine()
                    print "CEP (00000-000): "; c.cep = scanner.nextLine()
                    print "Descrição: "; c.descricao = scanner.nextLine()
                    print "Habilidades (separadas por vírgula): "
                    c.habilidades = scanner.nextLine().split(",").collect { it.trim() }
                    candidatoDAO.salvarCandidato(c)
                    break

                case 5:
                    println "\n--- Publicar Vaga ---"
                    def empresas = empresaDAO.listarEmpresas()
                    if (empresas.empty) {
                        println "Cadastre uma empresa primeiro!"
                        break
                    }
                    empresas.each { println "${it.id}: ${it.nome}" }
                    print "ID da Empresa: "; int idEmp = scanner.nextInt(); scanner.nextLine()

                    def v = new Vaga(empresaId: idEmp)
                    print "Título da Vaga: "; v.nome = scanner.nextLine()
                    print "Descrição: "; v.descricao = scanner.nextLine()
                    print "Competências Necessárias (vírgula): "
                    v.competencias = scanner.nextLine().split(",").collect { it.trim() }
                    vagaDAO.salvarVaga(v)
                    break

                case 6:
                    println "\n--- Vagas Disponíveis ---"
                    vagaDAO.listarVagas().each {
                        println "Vaga: ${it.nome} | Requisitos: ${it.competencias.join(', ')}"
                    }
                    break

                case 7:
                    println "\n--- Dicionário de Competências ---"
                    compDAO.listarCompetencias().each { println "- ${it}" }
                    break

                case 0:
                    println "Finalizando..."
                    break
            }
        }
    }
}
