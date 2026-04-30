package service

import domain.Vaga
import repository.VagaRepository
import spock.lang.Specification
import spock.lang.Unroll

class VagaServiceSpec extends Specification {
    VagaRepository repositorio = Mock()
    VagaService service = new VagaService(repositorio)

    private Vaga vagaValida() {
        return new Vaga(
                nome: "Desenvolvedor Java",
                descricao: "Vaga para desenvolvedor Java",
                empresaId: 1,
                competencias: ["Java", "Spring", "SQL"],
                local: "São Paulo"
        )
    }

    def "deve cadastrar vaga válida e chamar o repositório"() {
        given:
        Vaga vaga = vagaValida()

        when:
        service.salvarVaga(vaga)

        then:
        1 * repositorio.salvarVaga(vaga)
    }

    def "deve lançar exceção quando nome da vaga está vazio"() {
        given:
        Vaga vaga = vagaValida()
        vaga.nome = ""

        when:
        service.salvarVaga(vaga)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Nome da vaga é obrigatório"
        0 * repositorio.salvarVaga(_)
    }

    def "deve lançar exceção quando empresaId é inválido"() {
        given:
        Vaga vaga = vagaValida()
        vaga.empresaId = 0

        when:
        service.salvarVaga(vaga)

        then:
        IllegalArgumentException ex = thrown()
        ex.message.contains("ID de empresa válido")
        0 * repositorio.salvarVaga(_)
    }

    @Unroll
    def "deve rejeitar empresaId=#empresaId como inválido"() {
        given:
        Vaga vaga = vagaValida()
        vaga.empresaId = empresaId

        when:
        service.salvarVaga(vaga)

        then:
        thrown(IllegalArgumentException)

        where:
        empresaId | _
        0         | _
        -1        | _
        -99       | _
    }

    def "deve retornar a lista de vagas do repositório"() {
        given:
        repositorio.listarVagas() >> [vagaValida()]

        when:
        List<Vaga> resultado = service.listarVagas()

        then:
        resultado.size() == 1
        resultado[0].nome == "Desenvolvedor Java"
        resultado[0].empresaId == 1
    }
}
