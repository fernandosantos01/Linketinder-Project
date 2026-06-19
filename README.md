# 🔗 Linketinder

> Plataforma de conexão entre candidatos e empresas — inspirada no LinkedIn com mecânica de match estilo Tinder.

---

## 📋 Sobre o projeto

O **Linketinder** é uma plataforma desenvolvida em **Groovy** que permite o cadastro de candidatos, empresas e vagas de emprego, conectando perfis de candidatos às vagas com base em competências em comum.

O projeto possui duas interfaces de uso: uma **aplicação console** e um **backend com API REST**, ambas compartilhando as mesmas camadas de Service, Repository e DAO. Foi construído com foco em boas práticas de desenvolvimento, aplicando os princípios de **Clean Code**, **S.O.L.I.D**, o padrão arquitetural **MVC** e **Design Patterns** (Singleton, Factory Method, Repository e Dependency Injection).

---

## 🚀 Tecnologias

| Tecnologia            | Versão   | Uso                                      |
|-----------------------|----------|------------------------------------------|
| Groovy                | 5.0.0    | Linguagem principal                      |
| Java                  | 17+      | Plataforma de execução                   |
| PostgreSQL            | 15+      | Banco de dados relacional                |
| Apache Tomcat Embed   | 8.5.100  | Servidor HTTP embarcado para a API REST  |
| Groovy JSON           | 5.0.0    | Serialização e parsing de JSON           |
| Spock Framework       | 2.x      | Testes unitários                         |
| Gradle                | 8.x      | Build e gerenciamento de dependências    |

---

## ⚙️ Pré-requisitos

- Java 17 ou superior instalado
- PostgreSQL rodando localmente na porta `5432`
- Gradle instalado (ou usar o wrapper `./gradlew`)

---

## 🗄️ Configuração do banco de dados

1. Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE db_linketinder;
```

2. Execute os scripts na pasta `database/`:

```bash
psql -U seu_usuario -d db_linketinder -f database/schema.sql
psql -U seu_usuario -d db_linketinder -f database/match_inserts.sql
```

O `schema.sql` cria todas as tabelas (base + match). O `match_inserts.sql` popula o banco com dados de demonstração, incluindo matches confirmados.

3. Configure as variáveis de ambiente para a conexão:

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

---

## 🤝 Lógica de Match

### Como o match funciona

O Linketinder implementa uma lógica de curtida bidirecional: o match só ocorre quando **candidato e empresa se curtem mutuamente**, independentemente da ordem.

```
Candidato curte vaga  →  persiste em curtidas_candidato
Empresa curte candidato →  persiste em curtidas_empresa
                           + rastreio no BD
                           → se candidato curtiu vaga da empresa → MATCH
                           → persiste em matches
```

O rastreio acontece sempre que uma **empresa curte um candidato**: o sistema consulta `curtidas_candidato` para verificar se aquele candidato já curtiu alguma vaga pertencente a essa empresa. Se sim, o match é registrado.

O caminho inverso (candidato curte vaga depois de a empresa já tê-lo curtido) seguiria a mesma lógica: ao persistir a curtida do candidato, o sistema verifica se a empresa dona da vaga já curtiu esse candidato.

---

### Modelo de dados — tabelas de match

```
curtidas_candidato                curtidas_empresa
──────────────────                ────────────────
candidato_id  FK→candidatos       empresa_id    FK→empresas
vaga_id       FK→vagas            candidato_id  FK→candidatos
data_curtida  TIMESTAMP           data_curtida  TIMESTAMP
PK (candidato_id, vaga_id)        PK (empresa_id, candidato_id)
```

```
matches
───────
id           SERIAL PK
candidato_id FK→candidatos
empresa_id   FK→empresas
vaga_id      FK→vagas       ← vaga que originou o match
data_match   TIMESTAMP
UNIQUE (candidato_id, empresa_id)
```

A `UNIQUE (candidato_id, empresa_id)` em `matches` garante que um par só tenha um match registrado, mesmo que o candidato tenha curtido múltiplas vagas da mesma empresa. O `vaga_id` preserva qual foi a vaga que disparou o evento.

---

### MER — Diagrama de Entidade-Relacionamento

```
candidatos ──< candidato_competencias >── competencias
     │
     ├──< curtidas_candidato >── vagas ──< vaga_competencias >── competencias
     │                             │
     │                          empresa_id
     │                             │
     └──< curtidas_empresa  ── empresas
     │
     └──< matches >── empresas
                 └──> vagas
```

**Leitura:** um candidato pode curtir muitas vagas (`curtidas_candidato`); uma empresa pode curtir muitos candidatos (`curtidas_empresa`); quando há reciprocidade, um registro é criado em `matches` ligando candidato, empresa e a vaga de origem.

---

### Query de rastreio de match

Executada quando uma empresa curte um candidato para verificar se há reciprocidade:

```sql
SELECT cc.candidato_id,
       cc.vaga_id,
       v.nome AS vaga_nome
FROM curtidas_candidato cc
JOIN vagas v ON v.id = cc.vaga_id
WHERE cc.candidato_id = :candidato_id
  AND v.empresa_id    = :empresa_id
LIMIT 1;
```

Se retornar resultado, o match é registrado:

```sql
INSERT INTO matches (candidato_id, empresa_id, vaga_id)
VALUES (:candidato_id, :empresa_id, :vaga_id_encontrado);
```

---

### Listagem de matches

```sql
SELECT
    m.id            AS match_id,
    c.nome          AS candidato,
    e.nome          AS empresa,
    v.nome          AS vaga_origem,
    m.data_match
FROM matches m
JOIN candidatos c ON c.id = m.candidato_id
JOIN empresas   e ON e.id = m.empresa_id
JOIN vagas      v ON v.id = m.vaga_id
ORDER BY m.data_match;
```

---

## ▶️ Como rodar

Clone o repositório:

```bash
git clone https://github.com/fernandosantos01/Linketinder-Project.git
cd Linketinder-Project
```

**Aplicação console:**
```bash
./gradlew run
```

**Servidor REST (API na porta 8080):**
```bash
./gradlew runServer
```

**Testes unitários:**
```bash
./gradlew test
```

---

## 🗂️ Estrutura do projeto

```
database/
├── schema.sql          # Schema completo: tabelas base + tabelas de match
└── match_inserts.sql   # Dados de demonstração com matches confirmados

src/
├── main/groovy/
│   ├── controller/
│   │   ├── MenuController.groovy         # Loop do menu, delega para os controllers
│   │   ├── CandidatoController.groovy    # Coordena CandidatoService + CandidatoView
│   │   ├── EmpresaController.groovy      # Coordena EmpresaService + EmpresaView
│   │   ├── VagaController.groovy         # Coordena VagaService + VagaView
│   │   └── CompetenciaController.groovy  # Coordena CompetenciaService + CompetenciaView
│   ├── view/
│   │   ├── MenuView.groovy               # Exibe menu, lê opção do usuário
│   │   ├── CandidatoView.groovy          # Exibe candidatos, lê input de candidato
│   │   ├── EmpresaView.groovy            # Exibe empresas, lê input de empresa
│   │   ├── VagaView.groovy               # Exibe vagas, lê input de vaga
│   │   └── CompetenciaView.groovy        # Exibe competências
│   ├── dao/
│   │   ├── CandidatoDAO.groovy           # Persistência de candidatos
│   │   ├── EmpresaDAO.groovy             # Persistência de empresas
│   │   ├── VagaDAO.groovy                # Persistência de vagas
│   │   └── CompetenciaDAO.groovy         # Persistência e vínculo de competências
│   ├── domain/
│   │   ├── Candidato.groovy              # Entidade candidato
│   │   ├── Empresa.groovy                # Entidade empresa
│   │   └── Vaga.groovy                   # Entidade vaga
│   ├── repository/
│   │   ├── CandidatoRepository.groovy
│   │   ├── EmpresaRepository.groovy
│   │   ├── VagaRepository.groovy
│   │   ├── CompetenciaRepository.groovy
│   │   └── CompetenciaVinculoRepository.groovy
│   ├── service/
│   │   ├── CandidatoService.groovy       # Regras de negócio de candidatos
│   │   ├── EmpresaService.groovy         # Regras de negócio de empresas
│   │   ├── VagaService.groovy            # Regras de negócio de vagas
│   │   └── CompetenciaService.groovy     # Regras de negócio de competências
│   ├── util/
│   │   ├── IConnectionFactory.groovy     # Contrato da fábrica de conexões
│   │   ├── DatabaseFactory.groovy        # Fábrica — escolhe a implementação
│   │   ├── PostgresConnectionFactory.groovy  # Implementação PostgreSQL
│   │   ├── H2TestConnectionFactory.groovy    # Implementação H2 (testes)
│   │   └── DatabaseConnectionManager.groovy  # Singleton do gerenciador
│   └── Linketinder.groovy                # Ponto de entrada — raiz de composição
└── test/groovy/
    └── service/
        ├── CandidatoServiceSpec.groovy
        ├── EmpresaServiceSpec.groovy
        └── VagaServiceSpec.groovy
```

---

## 🏛️ Arquitetura

O projeto combina arquitetura em camadas com o padrão **MVC**, separando apresentação, controle e persistência em responsabilidades bem definidas:

```
 ┌─────────────────────────────────────────┐
 │                  VIEW                   │  ← exibe dados, lê input do usuário
 │  MenuView  CandidatoView  EmpresaView   │
 │  VagaView  CompetenciaView              │
 └──────────────────┬──────────────────────┘
                    │
 ┌──────────────────▼──────────────────────┐
 │               CONTROLLER                │  ← coordena View e Service
 │  MenuController   CandidatoController   │
 │  EmpresaController  VagaController      │
 └──────────────────┬──────────────────────┘
                    │
 ┌──────────────────▼──────────────────────┐
 │                SERVICE                  │  ← regras de negócio e validação
 └──────────────────┬──────────────────────┘
                    │
 ┌──────────────────▼──────────────────────┐
 │            REPOSITORY (interfaces)       │  ← contratos de persistência
 └──────────────────┬──────────────────────┘
                    │
 ┌──────────────────▼──────────────────────┐
 │                  DAO                    │  ← implementações de persistência
 └──────────────────┬──────────────────────┘
                    │
               PostgreSQL
```

**Cada camada depende apenas da camada imediatamente abaixo através de interfaces** — nunca de implementações concretas. O `Linketinder.groovy` atua exclusivamente como raiz de composição: instancia todos os objetos e conecta as dependências, sem conter nenhuma lógica de negócio ou apresentação.

---

## 🔄 Refatoração MVC

### O problema antes da refatoração

A classe `Linketinder.groovy` concentrava três responsabilidades distintas em um único arquivo com mais de 270 linhas:

- **Apresentação** — todos os `println` de menus, listagens e mensagens de erro
- **Leitura de input** — todo o `Scanner` e parsing de dados do usuário
- **Orquestração** — o `switch` que decidia qual ação executar

Isso violava diretamente o **Single Responsibility Principle** e tornava impossível testar ou reutilizar qualquer parte do código de interface sem arrastar toda a lógica junto.

---

### As correções realizadas

**1. Extração da camada View**

Todo código de apresentação e leitura de input foi movido para classes dedicadas em `view/`. Cada entidade ganhou sua própria View:

| Antes (em `Linketinder.groovy`) | Depois |
|---|---|
| `println "\n--- Candidatos ---"` | `CandidatoView.exibirCandidatos()` |
| `leitor.nextLine()` para ler campos | `CandidatoView.lerCandidato()` |
| `println "Empresa cadastrada!"` | `EmpresaView.exibirSucesso()` |
| `println "Opção inválida!"` | `MenuView.exibirOpcaoInvalida()` |

As Views recebem o `Scanner` por injeção de construtor — um único `Scanner` é criado no `main()` e compartilhado, evitando múltiplas instâncias abertas para `System.in`.

**2. Extração da camada Controller**

A lógica de coordenação — receber o input da View, chamar o Service e devolver o resultado para a View — foi movida para controllers dedicados:

```groovy
// antes: tudo junto em Linketinder.groovy
private static void cadastrarCandidato() {
    // ler input (View)
    // chamar service (Controller)
    // exibir resultado (View)
}

// depois: responsabilidades separadas
class CandidatoController {
    void cadastrar() {
        def candidato = view.lerCandidato()       // delega para View
        service.cadastrarCandidato(candidato)      // delega para Service
        view.exibirSucesso(candidato.nome)         // delega para View
    }
}
```

**3. Simplificação do `Linketinder.groovy`**

O arquivo principal passou de 270 linhas para menos de 45, contendo apenas a instanciação e conexão das dependências — sem nenhum `println`, sem leitura de input, sem regra de negócio.

---

### Como a refatoração tornou o código melhor

**Testabilidade:** cada View pode ser substituída por um mock nos testes. É possível testar um Controller passando uma View falsa que retorna dados fixos, sem precisar simular input do teclado.

**Manutenibilidade:** mudar a forma como candidatos são exibidos significa alterar apenas `CandidatoView` — sem risco de quebrar a lógica de cadastro ou persistência.

**Legibilidade:** `CandidatoController.cadastrar()` tem 5 linhas e descreve claramente o fluxo: ler → processar → exibir. Qualquer desenvolvedor entende o que acontece sem precisar rastrear o código de exibição misturado com o de negócio.

**Extensibilidade:** adicionar uma nova tela (ex: tela de match entre candidato e vaga) significa criar um novo Controller e uma nova View, sem modificar nenhuma classe existente.

---

## 🎨 Design Patterns

O projeto aplica quatro padrões de projeto que trabalham em conjunto para reduzir acoplamento, facilitar testes e tornar a troca de banco de dados uma operação de uma linha.

---

### 1. Singleton — `DatabaseConnectionManager`

**Categoria:** Criacional

**O que faz:** Garante que exista **uma única instância** do gerenciador de conexões em toda a aplicação. O construtor é privado — ninguém pode criar uma segunda instância acidentalmente.

```
DatabaseConnectionManager
  ├── construtor privado
  ├── instancia (campo estático)
  ├── inicializa(tipoDb)  → cria a instância uma única vez
  └── getInstancia()      → retorna sempre a mesma instância
```

**Por que o código ficou melhor:** antes de qualquer padrão, cada DAO chamava `DataBaseConnection.getConnection()` diretamente — a decisão de qual banco usar estava implícita e espalhada. Com o Singleton, essa decisão é tomada **uma vez** no `main()` e todos os componentes acessam o mesmo gerenciador, sem risco de inconsistência.

---

### 2. Factory Method — `DatabaseFactory` + `IConnectionFactory`

**Categoria:** Criacional

**O que faz:** Centraliza a criação de conexões com o banco de dados. A interface `IConnectionFactory` define o contrato (`createConnection()`), e a `DatabaseFactory` decide qual implementação instanciar com base no tipo informado.

```
DatabaseFactory.getFactory("POSTGRES") → PostgresConnectionFactory
DatabaseFactory.getFactory("TEST")     → H2TestConnectionFactory
```

**Por que o código ficou melhor:** os DAOs não sabem mais qual banco estão usando — eles recebem um `IConnectionFactory` e chamam `createConnection()`. Trocar de PostgreSQL para MySQL exige criar uma nova classe e mudar uma string no `main()`. Nenhum DAO precisa ser tocado.

---

### 3. Repository — interfaces em `repository/`

**Categoria:** Arquitetural (padrão DDD)

**O que faz:** Cada entidade do domínio tem uma interface que define o contrato de persistência (`CandidatoRepository`, `EmpresaRepository`, `VagaRepository`). Os DAOs são as implementações concretas dessas interfaces.

```
CandidatoRepository (interface)
  └── CandidatoDAO (implementação PostgreSQL)
  └── (qualquer outra implementação futura)
```

**Por que o código ficou melhor:** os Services dependem apenas das interfaces, nunca dos DAOs diretamente. Isso é o que permite os testes unitários usarem mocks do Spock sem banco de dados — o `CandidatoService` não consegue distinguir se está falando com um `CandidatoDAO` real ou com um mock.

---

### 4. Dependency Injection — construtores dos Services e DAOs

**Categoria:** Comportamental / princípio SOLID (Inversão de Dependência)

**O que faz:** Nenhuma classe instancia suas próprias dependências. Elas são recebidas pelo construtor. A única classe que cria objetos concretos é o `Linketinder.groovy`, que funciona como a **raiz de composição** do sistema.

```groovy
// Linketinder.groovy — único lugar com `new` de concretos
IConnectionFactory factory   = DatabaseConnectionManager.getInstancia().getFactory()
CompetenciaDAO competenciaDAO = new CompetenciaDAO(factory)
CandidatoRepository candidatoRepo = new CandidatoDAO(competenciaDAO, factory)
CandidatoService candidatoService = new CandidatoService(candidatoRepo)
```

**Por que o código ficou melhor:** `CandidatoService` não sabe que existe um PostgreSQL. `CandidatoDAO` não sabe que existe um `PostgresConnectionFactory`. Cada camada depende apenas do contrato (interface) da camada abaixo, tornando cada peça substituível e testável de forma independente.

---

### Como os quatro padrões se conectam

```
main() inicializa o Singleton (DatabaseConnectionManager)
  └── Singleton usa o Factory para criar IConnectionFactory
        └── IConnectionFactory é injetada nos DAOs (Dependency Injection)
              └── DAOs implementam interfaces do Repository
                    └── Services recebem essas interfaces (Dependency Injection)
```

O resultado é uma arquitetura onde **nenhuma camada está acoplada à implementação da camada abaixo** — apenas ao seu contrato.

---

## 🧹 Clean Code

As seguintes práticas de Clean Code foram aplicadas ao longo do projeto:

**Nomes auto-explicativos** — métodos e variáveis descrevem exatamente o que fazem, sem necessidade de comentários: `construirCandidatoDoResultSet`, `vincularCompetencias`, `buscarOuCriarCompetencia`.

**Funções pequenas** — cada método tem uma única responsabilidade. `salvarCandidato` delega para `inserirCandidato` e `vincularCompetencias`, mantendo o corpo principal legível.

**DRY (Don't Repeat Yourself)** — a lógica de busca e vínculo de competências foi centralizada em `CompetenciaDAO` com métodos parametrizados (`buscarCompetenciasDe`, `vincularCompetencias`), eliminando duplicação entre `CandidatoDAO` e `VagaDAO`.

**Sem comentários desnecessários** — o código é autoexplicativo. Comentários existem apenas para demarcar seções e justificar decisões não óbvias de design.

**Tratamento de erros** — exceções são sempre relançadas com contexto (`throw new RuntimeException("...", causa)`), preservando o stack trace original. Validações lançam `IllegalArgumentException` com mensagens claras.

**Constantes nomeadas** — strings de tabelas e colunas do banco são declaradas como constantes no topo de cada DAO (`TABELA_JUNCAO`, `COLUNA_ENTIDADE`, `COLUNA_COMPETENCIA`), evitando strings mágicas espalhadas pelo código.

---

## 🔷 S.O.L.I.D

### S — Single Responsibility Principle
Cada classe tem uma única razão para mudar. A validação de dados vive nos **Services**, não nos DAOs. Os DAOs são responsáveis exclusivamente por persistência. O `Main` é responsável apenas pela interface com o usuário.

### O — Open/Closed Principle
Os DAOs estão fechados para modificação de regras de negócio e abertos para extensão via novos métodos. Adicionar um novo comportamento (ex: buscar candidato por CPF) significa adicionar um método à interface e ao DAO, sem alterar o que já existe.

### L — Liskov Substitution Principle
Todos os DAOs implementam interfaces (`CandidatoRepository`, `EmpresaRepository`, etc.) e podem ser substituídos por qualquer outra implementação sem quebrar o sistema. Nos testes, os DAOs reais são substituídos por mocks do Spock sem nenhuma alteração nos Services.

### I — Interface Segregation Principle
As interfaces são enxutas e específicas. `CompetenciaRepository` expõe apenas `listarCompetencias()` para uso externo. `CompetenciaVinculoRepository` expõe os métodos de vínculo usados internamente pelos outros DAOs. Nenhuma classe é forçada a depender de métodos que não usa.

### D — Dependency Inversion Principle
Services dependem de interfaces, nunca de classes concretas. As dependências são injetadas pelo construtor. A única instanciação de classes concretas acontece no `Linketinder.groovy` (Main), que atua como a raiz de composição do sistema:

```groovy
IConnectionFactory factory        = DatabaseConnectionManager.getInstancia().getFactory()
CompetenciaDAO competenciaDAO     = new CompetenciaDAO(factory)
CandidatoRepository candidatoRepo = new CandidatoDAO(competenciaDAO, factory)
CandidatoService candidatoService = new CandidatoService(candidatoRepo)
```

---

## 🌐 API REST

### Estratégia e recursos utilizados

A API REST foi implementada **sem nenhum framework** (sem Spring, Grails ou Micronaut), utilizando apenas recursos do ecossistema Java/Groovy de mais baixo nível:

**Apache Tomcat Embed (`8.5.100`)** — servidor HTTP embarcado. Em vez de fazer o deploy de um arquivo `.war` em um Tomcat externo, o Tomcat é instanciado diretamente no código como uma biblioteca, iniciado programaticamente e encerrado com o processo. Isso elimina a necessidade de qualquer configuração de servidor externo.

**Java Servlets (`javax.servlet`)** — cada endpoint é uma classe que estende `HttpServlet`. Os métodos `doGet` e `doPost` são sobrescritos para tratar as requisições HTTP correspondentes. A versão 8.5 do Tomcat utiliza o pacote `javax.servlet` (versões 10+ migram para `jakarta.servlet`).

**Groovy JSON (`groovy-json`)** — biblioteca já presente no projeto, usada para:
- `JsonSlurper` — faz o parsing do corpo da requisição (JSON → objeto Groovy)
- `JsonOutput` — serializa os dados da resposta (objeto Groovy → JSON)

---

### Configuração

A dependência foi adicionada ao `build.gradle`:

```groovy
implementation 'org.apache.tomcat.embed:tomcat-embed-core:8.5.100'
```

Uma task Gradle foi criada para iniciar o servidor separadamente da aplicação console:

```groovy
task runServer(type: JavaExec) {
    group = 'application'
    description = 'Inicia o servidor REST do Linketinder na porta 8080'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'LinketinderServer'
}
```

O Tomcat é configurado e iniciado em `LinketinderServer.groovy`:

```groovy
Tomcat tomcat = new Tomcat()
tomcat.setPort(8080)
tomcat.getConnector()  // força a criação do conector HTTP

Context ctx = tomcat.addContext("", docBase)

Tomcat.addServlet(ctx, "candidatoServlet", new CandidatoServlet(candidatoService))
ctx.addServletMappingDecoded("/candidatos", "candidatoServlet")
// ... demais servlets

tomcat.start()
tomcat.getServer().await()  // mantém o processo vivo
```

Os Servlets recebem seus Services por injeção de construtor — a mesma camada de serviço usada pela aplicação console é reutilizada integralmente.

---

### Endpoints

| Método | Endpoint        | Descrição                        | Status de sucesso |
|--------|-----------------|----------------------------------|-------------------|
| GET    | `/candidatos`   | Lista todos os candidatos        | 200 OK            |
| POST   | `/candidatos`   | Cadastra um novo candidato       | 201 Created       |
| GET    | `/empresas`     | Lista todas as empresas          | 200 OK            |
| POST   | `/empresas`     | Cadastra uma nova empresa        | 201 Created       |
| GET    | `/vagas`        | Lista todas as vagas             | 200 OK            |
| POST   | `/vagas`        | Publica uma nova vaga            | 201 Created       |
| GET    | `/competencias` | Lista todas as competências      | 200 OK            |

---

### Exemplos de requisição

**POST /candidatos**
```json
{
  "nome": "Sandubinha",
  "cpf": "123.456.789-00",
  "email": "sand@email.com",
  "dataNascimento": "1990-05-15",
  "estado": "SP",
  "pais": "BRA",
  "habilidades": ["Java", "Groovy", "SQL"]
}
```

**POST /empresas**
```json
{
  "nome": "Tech Ltda",
  "cnpj": "12.345.678/0001-99",
  "email": "contato@tech.com",
  "descricao": "Empresa de tecnologia",
  "estado": "SP"
}
```

**POST /vagas**
```json
{
  "nome": "Dev Backend",
  "descricao": "Vaga para desenvolvedor backend",
  "local": "Remoto",
  "empresaId": 1,
  "competencias": ["Java", "SQL"]
}
```

---

### MVC na API REST

O padrão MVC é mantido na camada web:

```
Cliente (curl / frontend)
        ↓ HTTP
   Servlet (Controller)   ← recebe requisição, chama Service, retorna JSON
        ↓
   Service                ← regras de negócio e validação
        ↓
   Repository / DAO       ← persistência no PostgreSQL
```

Os Servlets não contêm lógica de negócio — apenas fazem o parsing do JSON de entrada, delegam para o Service e serializam a resposta. Erros de validação (`IllegalArgumentException`) retornam `400 Bad Request`; erros inesperados retornam `500 Internal Server Error`.

---

## 🧪 Testes

Os testes unitários cobrem a camada de **Services** usando o **Spock Framework** com mocks das interfaces de repositório — nenhum banco de dados é necessário para rodar os testes.

```bash
./gradlew test

# Relatório gerado em:
# build/reports/tests/test/index.html
```

Cenários cobertos por classe:

| Spec                    | Cenários testados                                              |
|-------------------------|----------------------------------------------------------------|
| `CandidatoServiceSpec`  | Cadastro válido, nome vazio, CPF vazio, data nula, listagem   |
| `EmpresaServiceSpec`    | Cadastro válido, nome vazio, CNPJ vazio, listagem             |
| `VagaServiceSpec`       | Cadastro válido, nome vazio, empresaId inválido (0, -1, -99)  |

---

## 👤 Autor

**Fernando Santos**