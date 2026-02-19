package domain

class Linketinder {
    static List<Candidato> listaCandidatos = []
    static List<Empresa> listaEmpresas = []

    static void main(String[] args) {
        inicializarInformacoes()
        boolean executarPrograma = true
        while (executarPrograma) {
            println "\n=== Bem-vindo ao Linketinder ==="
            println "1. LISTAR TODAS AS EMPRESAS"
            println "2. LISTAR TODOS OS CANDIDATOS"
            println "3. CADASTRAR NOVO CANDIDATO"
            println "4. CADASTRAR NOVA EMPRESA"
            println "5. SAIR"
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
                    executarPrograma = false
                    println "Saindo do programa. Até mais!"
                    break
                default:
                    println "Opção inválida. Tente novamente."
            }
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
