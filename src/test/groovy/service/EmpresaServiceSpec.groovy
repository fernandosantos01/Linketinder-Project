package service

import domain.Empresa
import repository.EmpresaRepository
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {
    EmpresaRepository repositorio = Mock()
    EmpresaService service = new EmpresaService(repositorio)

    private Empresa empresaValida() {
        return new Empresa(
                nome:  "Tech Ltda",
                cnpj:  "12.345.678/0001-99",
                email: "contato@tech.com"
        )
    }

    def "deve cadastrar empresa válida e chamar o repositório"() {
        given:
        Empresa empresa = empresaValida()

        when:
        service.cadastrarEmpresas(empresa)

        then:
        1 * repositorio.salvarEmpresa(empresa)
    }

    def "deve lançar exceção quando nome da empresa está vazio"() {
        given:
        Empresa empresa = empresaValida()
        empresa.nome = ""

        when:
        service.cadastrarEmpresas(empresa)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Nome da empresa é obrigatório"
        0 * repositorio.salvarEmpresa(_)
    }

    def "deve lançar exceção quando CNPJ está vazio"() {
        given:
        Empresa empresa = empresaValida()
        empresa.cnpj = ""

        when:
        service.cadastrarEmpresas(empresa)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "CNPJ é obrigatório"
        0 * repositorio.salvarEmpresa(_)
    }

    def "deve retornar a lista de empresas do repositório"() {
        given:
        repositorio.listarEmpresas() >> [empresaValida()]

        when:
        List<Empresa> resultado = service.listarEmpresas()

        then:
        resultado.size() == 1
        resultado[0].nome == "Tech Ltda"
    }
}
