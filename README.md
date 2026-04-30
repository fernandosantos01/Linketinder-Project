# 🔗 Linketinder

> Plataforma de conexão entre candidatos e empresas — inspirada no LinkedIn com mecânica de match estilo Tinder.

---

## 📋 Sobre o projeto

O **Linketinder** é uma aplicação console desenvolvida em **Groovy** que permite o cadastro de candidatos, empresas e vagas de emprego, conectando perfis de candidatos às vagas com base em competências em comum.

O projeto foi construído com foco em boas práticas de desenvolvimento, aplicando os princípios de **Clean Code** e o padrão **S.O.L.I.D** em toda a arquitetura.

---

## 🚀 Tecnologias

| Tecnologia       | Versão   | Uso                              |
|------------------|----------|----------------------------------|
| Groovy           | 4.x      | Linguagem principal              |
| Java             | 17+      | Plataforma de execução           |
| PostgreSQL       | 15+      | Banco de dados relacional        |
| Spock Framework  | 2.x      | Testes unitários                 |
| Gradle           | 8.x      | Build e gerenciamento de deps    |

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

2. Execute o script de criação das tabelas:

```sql
CREATE TABLE candidatos (
    id               SERIAL PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    data_nascimento  DATE         NOT NULL,
    email            VARCHAR(100),
    cpf              VARCHAR(14)  NOT NULL UNIQUE,
    pais             VARCHAR(50),
    estado           VARCHAR(2),
    cep              VARCHAR(9),
    descricao        TEXT
);

CREATE TABLE empresas (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    cnpj      VARCHAR(18)  NOT NULL UNIQUE,
    email     VARCHAR(100),
    descricao TEXT,
    pais      VARCHAR(50),
    estado    VARCHAR(2),
    cep       VARCHAR(9)
);

CREATE TABLE vagas (
    id         SERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    descricao  TEXT,
    local      VARCHAR(100),
    empresa_id INTEGER REFERENCES empresas(id)
);

CREATE TABLE competencias (
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE candidato_competencias (
    candidato_id   INTEGER REFERENCES candidatos(id),
    competencias_id INTEGER REFERENCES competencias(id),
    PRIMARY KEY (candidato_id, competencias_id)
);

CREATE TABLE vaga_competencias (
    vaga_id       INTEGER REFERENCES vagas(id),
    competencia_id INTEGER REFERENCES competencias(id),
    PRIMARY KEY (vaga_id, competencia_id)
);
```

3. Configure as variáveis de ambiente para a conexão:

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

---

## ▶️ Como rodar

Clone o repositório e execute:

```bash
git clone https://github.com/fernandosantos01/Linketinder-Project.git
cd Linketinder-Project

# Compilar e executar
./gradlew run

# Rodar os testes
./gradlew test
```

---

## 🗂️ Estrutura do projeto

```
src/
├── main/groovy/
│   ├── dao/
│   │   ├── CandidatoDAO.groovy       # Persistência de candidatos
│   │   ├── EmpresaDAO.groovy         # Persistência de empresas
│   │   ├── VagaDAO.groovy            # Persistência de vagas
│   │   └── CompetenciaDAO.groovy     # Persistência e vínculo de competências
│   ├── domain/
│   │   ├── Candidato.groovy          # Entidade candidato
│   │   ├── Empresa.groovy            # Entidade empresa
│   │   └── Vaga.groovy               # Entidade vaga
│   ├── repository/
│   │   ├── CandidatoRepository.groovy
│   │   ├── EmpresaRepository.groovy
│   │   ├── VagaRepository.groovy
│   │   ├── CompetenciaRepository.groovy
│   │   └── CompetenciaVinculoRepository.groovy
│   ├── service/
│   │   ├── CandidatoService.groovy   # Regras de negócio de candidatos
│   │   ├── EmpresaService.groovy     # Regras de negócio de empresas
│   │   ├── VagaService.groovy        # Regras de negócio de vagas
│   │   └── CompetenciaService.groovy # Regras de negócio de competências
│   ├── util/
│   │   └── DataBaseConnection.groovy # Gerenciamento de conexão com o banco
│   └── Linketinder.groovy            # Ponto de entrada — menu console
└── test/groovy/
    └── service/
        ├── CandidatoServiceSpec.groovy
        ├── EmpresaServiceSpec.groovy
        └── VagaServiceSpec.groovy
```

---

## 🏛️ Arquitetura

O projeto segue uma arquitetura em camadas com responsabilidades bem definidas:

```
Main (Linketinder)
     ↓
  Services        ← regras de negócio e validação
     ↓
 Repositories     ← interfaces (contratos)
     ↓
    DAOs           ← implementações de persistência
     ↓
 PostgreSQL
```

**Cada camada só conhece a camada imediatamente abaixo dela através de interfaces** — nunca de implementações concretas. Isso é o que permite trocar o banco de dados ou criar implementações falsas para testes sem alterar nenhuma regra de negócio.

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
CompetenciaDAO competenciaDAO   = new CompetenciaDAO()
CandidatoRepository candidatoRepo = new CandidatoDAO(competenciaDAO)
CandidatoService candidatoService = new CandidatoService(candidatoRepo)
```

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