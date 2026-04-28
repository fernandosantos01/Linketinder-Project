import dao.CandidatoDAO
import dao.VagaDAO
import dao.EmpresaDAO
import domain.Candidato
import domain.Vaga
import domain.Empresa
import util.DataBaseConnection
import java.time.LocalDate

class TesteDataBase {
    static void main(String[] args) {
        // 1. Instanciar os DAOs
        def candidatoDAO = new CandidatoDAO()
        def empresaDAO = new EmpresaDAO()
        def vagaDAO = new VagaDAO()

        println "--- INICIANDO TESTE DE INTEGRAÇÃO JDBC ---"

        try {
            // 2. SETUP: Garantir que as competências existam no banco
            // Se o seu DAO não cadastra competências novas, precisamos inseri-las manualmente para o teste
            setupCompetencias()

            // 3. TESTE CANDIDATO
            println "\n[1/2] Testando Candidato com Habilidades..."
            def novoCandidato = new Candidato(
                    nome: "Fernando JDBC Test",
                    email: "jdbc@testeeee.com",
                    cpf: "12845678921",
                    dataNascimento: LocalDate.of(1995, 5, 20),
                    pais: "Brasil",
                    estado: "Piauí",
                    cep: "64000-000",
                    descricao: "Especialista em JDBC",
                    habilidades: ["Java", "Groovy", "PostgreSQL"] // O teste real está aqui
            )

            candidatoDAO.salvarCandidato(novoCandidato)

            def candidatosDoBanco = candidatoDAO.listarCandidatos()
            def recuperado = candidatosDoBanco.find { it.email == "jdbc@testeee.com" }

            if (recuperado && !recuperado.habilidades.isEmpty()) {
                println "✅ SUCESSO: Candidato salvo e habilidades recuperadas: ${recuperado.habilidades}"
            } else {
                println "❌ FALHA: Candidato recuperado, mas a lista de habilidades está vazia!"
            }

            // 4. TESTE VAGA (Precisa de uma empresa primeiro)
            println "\n[2/2] Testando Vaga com Competências..."
            def empresa = new Empresa(
                    nome: "Tech Piauí", cnpj: "123356", email: "contato@tech.pi",
                    descricao: "Dev Shop", pais: "Brasil", estado: "PI", cep: "64000"
            )
            empresaDAO.salvarEmpresa(empresa)
            def empresaId = empresaDAO.listarEmpresas().find { it.cnpj == "123356" }?.id

            def novaVaga = new Vaga(
                    nome: "Desenvolvedor Backend",
                    descricao: "Vaga para testar JDBC",
                    local: "Teresina",
                    empresaId: empresaId,
                    competencias: ["Java", "PostgreSQL"]
            )

            vagaDAO.salvarVaga(novaVaga)

            def vagasDoBanco = vagaDAO.listarVagas()
            def vagaRecuperada = vagasDoBanco.find { it.nome == "Desenvolvedor Backend" }

            if (vagaRecuperada && !vagaRecuperada.competencias.isEmpty()) {
                println "✅ SUCESSO: Vaga salva e competências recuperadas: ${vagaRecuperada.competencias}"
            } else {
                println "❌ FALHA: Vaga recuperada, mas a lista de competências está vazia!"
            }

        } catch (Exception e) {
            println "🚨 ERRO CRÍTICO NO TESTE: ${e.message}"
            e.printStackTrace()
        }
    }

    // Método auxiliar para garantir que os nomes das competências existam na tabela 'competencias'
    static void setupCompetencias() {
        def sql = DataBaseConnection.getConnection()
        def nomes = ["Java", "Groovy", "PostgreSQL"]

        nomes.each { nome ->
            def stmt = sql.prepareStatement("INSERT INTO competencias (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING")
            stmt.setString(1, nome)
            stmt.executeUpdate()
            stmt.close()
        }
        sql.close()
    }
}