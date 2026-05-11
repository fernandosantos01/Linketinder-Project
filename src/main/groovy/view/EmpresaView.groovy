package view

import domain.Empresa

class EmpresaView {
    private final Scanner leitor

    EmpresaView(Scanner leitor) {
        this.leitor = leitor
    }

    void exibirEmpresas(List<Empresa> empresas) {
        println "\n--- Empresas ---"
        if (empresas.isEmpty()) {
            println "Nenhuma empresa cadastrada."
            return
        }
        empresas.each { empresa ->
            println "ID: ${empresa.id} | ${empresa.nome} (${empresa.cnpj})"
        }
    }

    Empresa lerEmpresa() {
        println "\n--- Nova Empresa ---"
        Empresa empresa = new Empresa()

        print "Nome: "
        empresa.nome = leitor.nextLine()

        print "CNPJ: "
        empresa.cnpj = leitor.nextLine()

        print "Email: "
        empresa.email = leitor.nextLine()

        print "Descrição: "
        empresa.descricao = leitor.nextLine()

        print "Estado (UF): "
        empresa.estado = leitor.nextLine()

        return empresa
    }

    void exibirSucesso(String nome) {
        println "Empresa '${nome}' cadastrada com sucesso!"
    }

    void exibirErro(String mensagem) {
        println "Ainda não foi possível salvar a empresa: ${mensagem}"
    }
}
