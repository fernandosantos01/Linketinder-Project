package service

import domain.Candidato
import repository.CandidatoRepository
import spock.lang.Specification

import java.time.LocalDate


class CandidatoServiceSpec extends Specification {
    private final CandidatoRepository repository = Mock()
    private final CandidatoService service = new CandidatoService(repository)

    private Candidato candidatoValido() {
        return new Candidato(
                nome: "Maria Silva",
                cpf: "123.456.789-00",
                dataNascimento: LocalDate.of(1995, 6, 15),
                email: "maria@email.com",
                estado: "PI",
                habilidades: ["Java", "Groovy"]
        )
    }

    def "deve cadastrar candidato válido e chamar o repositorio"() {
        given:
        Candidato candidato = candidatoValido()

        when:
        service.cadastrarCandidato(candidato)

        then:
        1 * repository.salvarCandidato(candidato)
    }

    def "deve lançar excessao quando nome está vazio"() {
        given:
        Candidato candidato = candidatoValido()
        candidato.nome = ""

        when:
        service.cadastrarCandidato(candidato)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Nome do candidato é obrigatório"
        0 * repository.salvarCandidato(_)
    }

    def "deve lançar exceção quando nome contém só espaços"() {
        given:
        Candidato candidato = candidatoValido()
        candidato.nome = "     "

        when:
        service.cadastrarCandidato(candidato)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Nome do candidato é obrigatório"
        0 * repository.salvarCandidato(_)
    }

    def "deve lançar exceção quando CPF está vazio"() {
        given:
        Candidato candidato = candidatoValido()
        candidato.cpf = ""

        when:
        service.cadastrarCandidato(candidato)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "CPF é obrigatório"
        0 * repository.salvarCandidato(_)
    }

    def "deve lançar exceção quando data de nascimento é nula"() {
        given:
        Candidato candidato = candidatoValido()
        candidato.dataNascimento = null

        when:
        service.cadastrarCandidato(candidato)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Data de nascimento é obrigatória"
        0 * repository.salvarCandidato(_)
    }

    def "deve retornar a lista de candidatos do repositório"() {
        given:
        List<Candidato> lista = [candidatoValido()]

        repository.listarCandidatos() >> lista

        when:
        List<Candidato> resultado = service.listarCandidatos()

        then:
        resultado.size() == 1
        resultado[0].nome == "Maria Silva"
    }
}
