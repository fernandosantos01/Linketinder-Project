import dao.CandidatoDAO
import dao.CompetenciaDAO
import dao.EmpresaDAO
import dao.VagaDAO
import domain.Candidato
import domain.Empresa
import domain.Vaga
import util.DataBaseConnection
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Stepwise
import java.time.LocalDate

// A anotação @Stepwise garante que os testes rodem na ordem que foram escritos.
// Isso é útil em banco de dados quando um teste depende de outro (ex: listar depois de salvar).
@Stepwise
class DAOSpec extends Specification {

    // @Shared significa que a mesma instância do DAO será usada para todos os testes desta classe
    @Shared
    EmpresaDAO empresaDAO = new EmpresaDAO()
    @Shared
    CandidatoDAO candidatoDAO = new CandidatoDAO()
    @Shared
    def sql = DataBaseConnection.getConnection()

    // --- SETUP GLOBAL DAS COMPETÊNCIAS ---
    // Antes de todos os testes começarem, garantimos que as competências básicas existam.
    def setupSpec() {
        def conn = DataBaseConnection.getConnection()
        def stmt = conn.prepareStatement("INSERT INTO competencias (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING")
        ["Java", "Groovy", "Spock"].each { nome ->
            stmt.setString(1, nome)
            stmt.executeUpdate()
        }
        stmt.close()
        conn.close()
    }

    // --- TESTES DA EMPRESA ---

    def "deve salvar uma nova Empresa no banco de dados e recuperá-la"() {
        setup: "Garante banco limpo e cria um objeto Empresa válido"

        // A MÁGICA AQUI: Apaga a empresa de testes antes mesmo de começar!
        try {
            sql.createStatement().execute("DELETE FROM empresas WHERE cnpj = '999888777'")
        } catch (Exception e) {
            // Se der erro (ex: não existir), ele apenas ignora e segue a vida
        }

        def novaEmpresa = new Empresa(
                nome: "Spock Tech",
                cnpj: "999888777",
                email: "contato@spock.tech",
                descricao: "Empresa de testes",
                pais: "Brasil",
                estado: "SP",
                cep: "01000-000"
        )

        when: "O método salvar é chamado"
        empresaDAO.salvarEmpresa(novaEmpresa)
        def empresasNoBanco = empresaDAO.listarEmpresas()
        println "RAIO-X: Total de empresas listadas: ${empresasNoBanco.size()}"
        if (!empresasNoBanco.isEmpty()) {
            println "RAIO-X: CNPJs encontrados na lista: " + empresasNoBanco.collect { it.cnpj }
        }

        then: "A empresa deve aparecer na lista com os mesmos dados e ID > 0"
        def empresaRecuperada = empresasNoBanco.find { it.cnpj == "999888777" }

        empresaRecuperada != null
        empresaRecuperada.id > 0
        empresaRecuperada.nome == "Spock Tech"
        empresaRecuperada.email == "contato@spock.tech"

        cleanup: "Apagamos a empresa para manter o banco limpo para outras execuções"
        if (empresaRecuperada) {
            sql.createStatement().execute("DELETE FROM empresas WHERE id = ${empresaRecuperada.id}")
        }
    }

    // --- TESTES DO CANDIDATO (COM HABILIDADES) ---

    def "deve salvar um Candidato e suas habilidades na tabela de junção"() {
        setup: "Criamos um Candidato com habilidades"
        def novoCandidato = new Candidato(
                nome: "Mestre Spock",
                email: "spock@enterprise.com",
                cpf: "11122233344",
                dataNascimento: LocalDate.of(1980, 1, 1),
                pais: "Vulcan",
                estado: "N/A",
                cep: "00000",
                descricao: "Lógico",
                habilidades: ["Java", "Groovy"] // Note: Estas competências foram inseridas no setupSpec
        )

        when: "Salvamos o candidato"
        candidatoDAO.salvarCandidato(novoCandidato)

        then: "O candidato é salvo e a lista de habilidades é populada corretamente no JOIN"
        def candidatosNoBanco = candidatoDAO.listarCandidatos()
        def recuperado = candidatosNoBanco.find { it.email == "spock@enterprise.com" }

        recuperado != null
        recuperado.id > 0
        recuperado.habilidades.size() == 2
        recuperado.habilidades.contains("Java")
        recuperado.habilidades.contains("Groovy")

        cleanup: "Limpamos as dependências na ordem correta (primeiro N:N, depois o Candidato)"
        if (recuperado) {
            // A restrição de FK impede de apagar o candidato antes de apagar a ligação
            sql.createStatement().execute("DELETE FROM candidato_competencias WHERE candidato_id = ${recuperado.id}")
            sql.createStatement().execute("DELETE FROM candidatos WHERE id = ${recuperado.id}")
        }
    }
    // -------------------------------------------------------------------------
    // --- TESTES DO COMPETENCIA_DAO ---
    // -------------------------------------------------------------------------

    def "deve buscar uma competencia existente ou criar uma nova se nao existir"() {
        setup: "Instanciamos o DAO de Competencias e preparamos nomes de teste"
        def compDAO = new CompetenciaDAO() // Usa o DAO recém-criado
        String compExistente = "Java" // Já inserida no setupSpec
        String compNova = "SpockTestingMaster" // Nome maluco para garantir que não existe
        def conn = DataBaseConnection.getConnection()

        when: "Pedimos para buscar ou criar ambas as competências"
        int idExistente = compDAO.buscarOuCriarCompetencia(compExistente, conn)
        int idNova = compDAO.buscarOuCriarCompetencia(compNova, conn)

        // Vamos listar tudo para garantir que a nova foi adicionada
        def todasAsCompetencias = compDAO.listarCompetencias()

        then: "Os IDs devem ser válidos e a nova competência deve estar na lista"
        idExistente > 0
        idNova > 0
        idExistente != idNova // Garantir que não pegou o mesmo ID por engano
        todasAsCompetencias.contains(compExistente)
        todasAsCompetencias.contains(compNova)

        cleanup: "Apagamos SOMENTE a competência nova para não quebrar o banco"
        conn.close()
        // A 'Java' não apagamos porque outras partes do sistema podem usar
        if (idNova > 0) {
            sql.createStatement().execute("DELETE FROM competencias WHERE id = ${idNova}")
        }
    }


    // -------------------------------------------------------------------------
    // --- TESTES DO VAGA_DAO ---
    // -------------------------------------------------------------------------

    def "deve salvar uma nova Vaga e vincular suas competencias corretamente"() {
        setup: "Garantimos uma empresa válida no banco para ser a 'dona' da vaga"
        def vagaDAO = new VagaDAO()

        // 1. Limpeza preventiva (se uma execução anterior falhou)
        try {
            sql.createStatement().execute("DELETE FROM empresas WHERE cnpj = '123123123'")
        } catch (Exception e) {
        }

        // 2. Cria a empresa dona da vaga
        def empresaDona = new Empresa(
                nome: "Vaga Test Corp", cnpj: "123123123", email: "vaga@test.com",
                descricao: "RH", pais: "BR", estado: "RJ", cep: "20000"
        )
        empresaDAO.salvarEmpresa(empresaDona)

        // Recupera o ID da empresa recém-criada
        int idEmpresa = empresaDAO.listarEmpresas().find { it.cnpj == "123123123" }?.id

        // 3. Monta a vaga de testes ligada a essa empresa
        def novaVaga = new Vaga(
                nome: "Desenvolvedor Spock Senior",
                descricao: "Vaga criada via teste automatizado",
                local: "Remoto",
                empresaId: idEmpresa,
                competencias: ["Java", "Groovy", "SQL"] // O DAO inteligente vai criar 'SQL' se não existir
        )

        when: "A vaga é salva no banco de dados"
        vagaDAO.salvarVaga(novaVaga)

        // Busca a vaga de volta do banco para conferir
        def vagasNoBanco = vagaDAO.listarVagas()
        def vagaRecuperada = vagasNoBanco.find { it.nome == "Desenvolvedor Spock Senior" }

        then: "A vaga deve ter sido salva, possuir um ID e ter exatamente 3 competencias ligadas"
        vagaRecuperada != null
        vagaRecuperada.id > 0
        vagaRecuperada.empresaId == idEmpresa
        vagaRecuperada.competencias.size() == 3
        vagaRecuperada.competencias.contains("SQL")

        cleanup: "Fazemos a faxina reversa: Tabela N:N -> Vaga -> Empresa"
        if (vagaRecuperada) {
            // A ordem de exclusão aqui é CRÍTICA por causa das chaves estrangeiras (Foreign Keys)
            sql.createStatement().execute("DELETE FROM vaga_competencias WHERE vaga_id = ${vagaRecuperada.id}")
            sql.createStatement().execute("DELETE FROM vagas WHERE id = ${vagaRecuperada.id}")
        }
        if (idEmpresa) {
            sql.createStatement().execute("DELETE FROM empresas WHERE id = ${idEmpresa}")
        }
    }
}