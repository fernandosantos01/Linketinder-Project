package domain

class Linketinder {
    static List<Candidato> listaCandidatos = []
    static List<Empresa> listaEmpresas = []
    static List<Curtida> listaCurtidas = []

    static void main(String[] args) {
        inicializarInformacoes()
        boolean executarPrograma = true
        while (executarPrograma) {
            println "\n=== Bem-vindo ao Linketinder ==="
            println "1. LISTAR TODAS AS EMPRESAS"
            println "2. LISTAR TODOS OS CANDIDATOS"
            println "3. CADASTRAR NOVO CANDIDATO"
            println "4. CADASTRAR NOVA EMPRESA"
            println "5. CURTIR EMPRESA"
            println "6. CURTIR CANDIDATO"
            println "7. LISTAR CANDIDATOS INTERESSADOS EM UMA EMPRESA"
            println "8. LISTAR EMPRESAS INTERESSADAS EM UM CANDIDATO(anonimo)"
            println "9. LISTAR MATCHES"
            println "10. SAIR"
            print "Escolha uma opção: "
            String escolha = System.in.newReader().readLine()

            switch (escolha) {
                case "1":
                    imprimirEmpresas()
                    break
                case "2":
                    imprimirCandidatos()
                    break
                case "3":
                    cadastrarCandidato()
                    break
                case "4":
                    cadastrarEmpresa()
                    break
                case "5":
                    curtirEmpresa()
                    break
                case "6":
                    curtirCandidato()
                    break
                case "7":
                    verCurtidasNaEmpresa()
                    break
                case "8":
                    verCurtidasNoCandidato()
                    break
                case "9":
                    listarMatches()
                    break
                case "10":
                    executarPrograma = false
                    println "Saindo do programa. Até mais!"
                    break
                default:
                    println "Opção inválida. Tente novamente."
            }
        }
    }

    static void listarMatches() {
        def matches = listaCurtidas.findAll { it.isMatch() }

        if (!matches) {
            println "Ainda não ocorreu nenhum match no sistema."
            return
        }
        matches.each { match ->
            println "MATCH: A empresa ${match.empresa.nome} e o(a) candidato(a) ${match.candidato.nome} deram match!"
            println "   📧 Contacto do Candidato: ${match.candidato.email}"
            println "   📧 Contacto da Empresa: ${match.empresa.email}\n"
        }
    }

    static void verCurtidasNaEmpresa() {
        println "Qual o nome da sua empresa"
        String nomeEmpresa = System.in.newReader().readLine()

        Empresa empresa = listaEmpresas.find { it.nome.equalsIgnoreCase(nomeEmpresa) }

        if (!empresa) {
            println "Empresa não encontrada!"
            return
        }
        def curtidasRecebidas = listaCurtidas.findAll { it.empresa == empresa && it.candCurtiu }

        if (curtidasRecebidas.isEmpty()) {
            println "Sua empresa não recebeu curtidas"
            return
        }
        println "===== CANDIDATOS QUE CURTIRAM A ${empresa.nome.toUpperCase()} ======="
        println "Modo de Recrutamento às Cegas ativado: Exibindo apenas competências."

        curtidasRecebidas.eachWithIndex { curtida, index ->
            Candidato candidato = curtida.candidato
            println "Candidato Anônimo #${index + 1}"
            println "Habilidades: ${candidato.habilidades.join(', ')}"
            println "Descrição: ${candidato.descricao}\n"
        }
    }

    static void verCurtidasNoCandidato() {
        println "Nome Candidato: "
        String nome = System.in.newReader().readLine()

        Candidato candidato = listaCandidatos.find { it.nome.equalsIgnoreCase(nome) }

        if (!candidato) {
            println "Candidato não encontrado!"
            return
        }
        def curtidasRecebidas = listaCurtidas.findAll { it.candidato == candidato && it.empCurtiu }
        if (!curtidasRecebidas) {
            println "Você ainda não recebeu curtidas de empresas."
            return
        }
        println "\n=== EMPRESAS DE OLHO EM VOCÊ, ${candidato.nome.toUpperCase()} ==="
        curtidasRecebidas.each { curtida ->
            println curtida.empresa
        }
    }

    static void curtirEmpresa() {
        imprimirEmpresas()
        println("Digite o número da empresa que deseja curtir: ")
        int numeroEmpresa = System.in.newReader().readLine().toInteger()
        if (numeroEmpresa < 1 || numeroEmpresa > listaEmpresas.size()) {
            println "Número de empresa inválido. Operação cancelada."
            return
        }
        Empresa empresa = listaEmpresas[numeroEmpresa - 1]
        println "Nome do Candidato que curtiu a empresa: "
        String nomeCandidato = System.in.newReader().readLine()
        Candidato candidato = listaCandidatos.find { it.nome.equalsIgnoreCase(nomeCandidato) }
        if (!candidato) {
            println "Candidato não encontrado. Verifique o nome e tente novamente."
            return
        }
        Curtida curtida = listaCurtidas.find { it.candidato == candidato && it.empresa == empresa }
        if (!curtida) {
            curtida = new Curtida(candidato, empresa)
            listaCurtidas << curtida
        }
        curtida.candCurtiu = true
        println "Empresa curtida com sucesso!"
        if (curtida.isMatch()) {
            println "Parabéns! Você tem um match com a empresa ${empresa.nome}!"
        }

    }

    static void curtirCandidato() {
        imprimirCandidatos()
        println("Digite o número do candidato que deseja curtir: ")
        int numeroCandidato = System.in.newReader().readLine().toInteger()
        if (numeroCandidato < 1 || numeroCandidato > listaCandidatos.size()) {
            println "Número de candidato inválido. Tente novamente."
            return
        }
        Candidato candidato = listaCandidatos[numeroCandidato - 1]
        println "Nome da Empresa que curtiu o candidato: "
        String nomeEmpresa = System.in.newReader().readLine()
        Empresa empresa = listaEmpresas.find { it.nome.equalsIgnoreCase(nomeEmpresa) }
        if (!empresa) {
            println "Empresa não encontrada."
            return
        }
        Curtida curtida = listaCurtidas.find { it.candidato == candidato && it.empresa == empresa }
        if (!curtida) {
            curtida = new Curtida(candidato, empresa)
            listaCurtidas << curtida
        }
        curtida.empCurtiu = true
        println "Candidato curtido com sucesso!"
        if (curtida.isMatch()) {
            println "Parabéns! Você tem um match com o candidato ${candidato.nome}!"
        }
    }


    static void imprimirEmpresas() {
        println "\n=== EMPRESAS ==="
        listaEmpresas.each { println it }
    }

    static void imprimirCandidatos() {
        println "\n=== CANDIDATOS ==="
        listaCandidatos.each { println it }
    }

    static void cadastrarCandidato() {
        println "\n==== CADASTRAR NOVO CANDIDATO ===="
        print "Nome: "
        String nome = System.in.newReader().readLine()
        println "Email: "
        String email = System.in.newReader().readLine()
        println "CPF: "
        String cpf = System.in.newReader().readLine()
        println "Idade: "
        def idade = System.in.newReader().readLine().toInteger()
        println "Estado: "
        String estado = System.in.newReader().readLine()
        println "CEP: "
        String cep = System.in.newReader().readLine()
        println "Descrição: "
        String descricao = System.in.newReader().readLine()
        println("Habilidades (separadas por vírgula): ")
        List<String> habilidades = System.in.newReader().readLine().split(",").collect { it.trim() }


        Candidato novoCandidato = new Candidato(nome: nome,
                email: email,
                cpf: cpf,
                idade: idade,
                estado: estado,
                cep: cep,
                descricao: descricao,
                habilidades: habilidades)

        listaCandidatos << novoCandidato
        println "Candidato cadastrado com sucesso!"
    }

    static void cadastrarEmpresa() {
        println "=====CADASTRAR NOVA EMPRESA======="
        println "Nome: "
        String nome = System.in.newReader().readLine()
        println("Email: ")
        String email = System.in.newReader().readLine()
        println("CNPJ: ")
        String cnpj = System.in.newReader().readLine()
        println("País: ")
        String pais = System.in.newReader().readLine()
        println("Estado: ")
        String estado = System.in.newReader().readLine()
        println("CEP: ")
        String cep = System.in.newReader().readLine()
        println("Descrição: ")
        String descricao = System.in.newReader().readLine()
        println("Habilidades Esperadas (separadas por vírgula): ")
        List<String> habilidades = System.in.newReader().readLine().split(",").collect { it.trim() }

        Empresa novaEmpresa = new Empresa(
                nome: nome,
                email: email,
                cnpj: cnpj,
                pais: pais,
                estado: estado,
                cep: cep,
                descricao: descricao,
                habilidades: habilidades
        )
        listaEmpresas << novaEmpresa
        println "Empresa Cadastada com sucesso!"
    }

    static void inicializarInformacoes() {
        listaCandidatos << new Candidato(nome: "Fernando",
                email: "fernando@gmail.com",
                cpf: "123.456.789-00",
                idade: 25,
                estado: "PI",
                cep: "64410-000",
                descricao: "Desenvolvedor Java com experiência em Spring Boot e Microservices",
                habilidades: ["Java", "Spring Boot", "Microservices"])

        listaCandidatos << new Candidato(nome: "Marta",
                email: "marta@gmail.com",
                cpf: "987.654.321-00",
                idade: 35,
                estado: "SP",
                cep: "01000-000",
                descricao: "Desenvolvedora Java com experiência em Docker e Kubernetes",
                habilidades: ["Java", "Docker", "Kubernetes"],)

        listaCandidatos << new Candidato(nome: "Maria",
                email: "maria@gmail.com",
                cpf: "456.789.123-00",
                idade: 30,
                estado: "RJ",
                cep: "20000-000",
                descricao: "Desenvolvedora Java com experiência em AWS e Cloud Computing",
                habilidades: ["Java", "AWS"])

        listaCandidatos << new Candidato(nome: "João",
                email: "joao@gmail.com",
                cpf: "321.654.987-00",
                idade: 28,
                estado: "MG",
                cep: "30000-000",
                descricao: "Desenvolvedor Java com experiência em Microservices e Cloud Native",
                habilidades: ["Java", "Spring Boot", "Microservices"])

        listaCandidatos << new Candidato(nome: "Ana",
                email: "ana@gmail.com",
                cpf: "654.321.987-00",
                idade: 22,
                estado: "RS",
                cep: "90000-000",
                descricao: "Desenvolvedora Java com experiência em AWS e Cloud Computing",
                habilidades: ["Java", "AWS"])
        listaEmpresas << new Empresa(nome: "Tech Solutions",
                email: "tech@gmail.com",
                cnpj: "12.345.678/0001-00",
                pais: "Brasil",
                estado: "SP",
                cep: "01000-000",
                descricao: "Estamos contratando desenvolvedores Java com experiência em Spring Boot.",
                habilidades: ["Java", "Spring Boot"])

        listaEmpresas << new Empresa(nome: "Innovatech",
                email: "innovatech@gmail.com",
                cnpj: "98.765.432/0001-00",
                pais: "Brasil",
                estado: "RJ",
                cep: "20000-000",
                descricao: "Procuramos profissionais Java especializados em Microservices.",
                habilidades: ["Java", "Microservices"])

        listaEmpresas << new Empresa(nome: "DevWorks",
                email: "devworks@gmail.com",
                cnpj: "56.789.123/0001-00",
                pais: "Brasil",
                estado: "MG",
                cep: "30000-000",
                descricao: "Vaga para desenvolvedores Java com conhecimento em Kubernetes.",
                habilidades: ["Java", "Kubernetes"])
        listaEmpresas << new Empresa(nome: "CloudNet",
                email: "CloudNet@gmail.com",
                cnpj: "34.567.890/0001-00",
                pais: "Brasil",
                estado: "RS",
                cep: "90000-000",
                descricao: "Estamos contratando desenvolvedores Java com experiência em AWS.",
                habilidades: ["Java", "AWS"])
        listaEmpresas << new Empresa(nome: "ZG Soluções",
                email: "zgsolucoes@gmail.com",
                cnpj: "11.223.344/0001-55",
                pais: "Brasil",
                estado: "PR",
                cep: "80000-000",
                descricao: "Buscamos desenvolvedores Java com experiência em Docker.",
                habilidades: ["Java", "Docker"])
    }
}
