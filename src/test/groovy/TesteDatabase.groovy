import dao.CandidatoDAO
import dao.VagaDAO
import domain.Candidato
import domain.Vaga
import java.time.LocalDate

class TesteDatabase {
    static void main(String[] args) {
        def candidatoDAO = new CandidatoDAO()
        def vagaDAO = new VagaDAO()

        println "--- Testando Conexão e Listagem ---"
        try {
            def candidatos = candidatoDAO.listarCandidatos()
            println "Candidatos no banco: ${candidatos.size()}"

            def vagas = vagaDAO.listarVagas()
            println "Vagas no banco: ${vagas.size()}"

            println "\n--- Testando Inserção ---"
            // Teste de inserção de candidato
            def novo = new Candidato(
                    nome: "Teste Conexao",
                    email: "teste@db.com",
                    cpf: "00000000000",
                    dataNascimento: LocalDate.of(2000, 1, 1),
                    pais: "Brasil",
                    estado: "PI",
                    cep: "64000-000",
                    descricao: "Candidato de teste para validar DAO"
            )
            candidatoDAO.salvarCandidato(novo)
            println "Candidato de teste inserido com sucesso!"

        } catch (Exception e) {
            println "FALHA NO TESTE: ${e.message}"
            e.printStackTrace()
        }
    }
}