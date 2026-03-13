package test

import domain.Candidato
import domain.Curtida
import domain.Empresa
import domain.Linketinder
import spock.lang.Specification

class TestLinketinder extends Specification {
    def setup() {
        Linketinder.listaCandidatos.clear()
        Linketinder.listaEmpresas.clear()
        Linketinder.listaCurtidas.clear()
    }

    def "deve adicionar um candidato na lista"() {
        given: "um candidato com dados validados e instanciado"
        def candidato = new Candidato(nome: "Antonio",
                email: "antonio@gmail.com",
                cpf: "123.456.789-00",
                idade: 25,
                estado: "PI",
                cep: "64410-000",
                descricao: "Desenvolvedor Java com experiência em Spring Boot e Microservices",
                habilidades: ["Java", "Spring Boot", "Microservices"])
        when: "Inserir o candidato na lista"
        Linketinder.adicionarCandidato(candidato)

        then: "a lista de candidatos deve aumentar para tamanho 1"
        Linketinder.listaCandidatos.size() == 1

        and: "Os dados do candidato na lista devem corresponder ao inserido"
        Linketinder.listaCandidatos.get(0).nome.equalsIgnoreCase("Antonio")
    }

    def "deve inserir uma empresa na lista"() {
        given: "instanciar uma empresa com dados validados"
        def empresa = new Empresa(nome: "Tech Solutions",
                email: "tech@gmail.com",
                cnpj: "12.345.678/0001-00",
                pais: "Brasil",
                estado: "SP",
                cep: "01000-000",
                descricao: "Estamos contratando desenvolvedores Java com experiência em Spring Boot.",
                habilidades: ["Java", "Spring Boot"])

        when: "o metodo inserir empresa deve ser invocado"
        Linketinder.adicionarEmpresa(empresa)

        then: "o tamanho da lista de empresa deve aumentar para 1"
        Linketinder.listaEmpresas.size() == 1

        and: "os dados da empresa devem corresponder"
        Linketinder.listaEmpresas.get(0).nome.equalsIgnoreCase("Tech Solutions")
    }

    def "deve testar a funcionalidade de só o candidato curtir(sem match)"() {
        given: "instaciar um candidato e uma empresa"
        def candidato = new Candidato(nome: "Teste")
        def empresaCurtida = new Empresa(nome: "TesteEmpresa")
        def curtida = new Curtida(candidato, empresaCurtida)

        when: "o metodo curtir empresa deve ser invocado"
        curtida.candCurtiu = true
        curtida.empCurtiu = false

        then: "o metodo match deve retornar false pois so o candidato curtiu"
        !curtida.isMatch()
    }

    def "testar funcionalidade de só a empresa curtir(sem match)"() {
        given: "instanciar a empresa e um candidato"
        def empresa = new Empresa(nome: "Teste")
        def candidato = new Candidato(nome: "Teste")
        def curtida = new Curtida(candidato, empresa)

        when: "o metodo curtir candidato deve invocado"
        curtida.empCurtiu = true
        curtida.candCurtiu = false

        then: "o metodo match deve retornar false"
        !curtida.isMatch()
    }

    def "deve testar a funcionalidade isMatch para true"() {
        given: "instanciar candidato e empresa e curtida"
        def candidato = new Candidato(nome: "Teste")
        def empresa = new Empresa(nome: "Teste")
        def curtida = new Curtida(candidato, empresa)

        when: "o metodo curtir deve ser chamado"
        curtida.candCurtiu = true
        curtida.empCurtiu = true

        then: "o match deve ocorrer"
        curtida.isMatch()
    }
}
