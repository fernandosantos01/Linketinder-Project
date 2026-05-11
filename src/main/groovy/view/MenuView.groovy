package view

class MenuView {
    private final Scanner leitor

    MenuView(Scanner leitor) {
        this.leitor = leitor
    }

    void exibirOpcoes() {
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

    int lerOpcao() {
        try {
            int opcao = leitor.nextInt()
            leitor.nextLine()
            return opcao
        } catch (Exception ignored) {
            println "Por favor, digite um número válido."
            leitor.nextLine()
            return -1
        }
    }

    void exibirFinalizando() {
        println "Finalizando..."
    }

    void exibirOpcaoInvalida() {
        println "Opção inválida!"
    }
}
