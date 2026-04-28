# Linketinder (MVP) - ZG-Hero

Projeto backend em **Groovy** para simular um sistema de recrutamento no estilo "match", com execução via terminal e persistência em banco de dados PostgreSQL.

## Atualizações Recentes

### Commits Implementados
- **`c2066e9`** - `refactor: configuração da dependência do gradle` - Atualização de dependências para compatibilidade com Java 21 (Groovy 5.0.0, Spock 2.4)
- **`7d8d804`** - `docs: atualiza README com novidades da persistencia JDBC` - Documentação de DAOs e persistência
- **`d83f5ac`** - `feat: refatora menu principal para usar persistencia em banco de dados` - Menu interativo com BD

### Refatoração Aplicada (Sprint Atual)
- ✅ **Nomes Autoexplicativos**: Renomeação de variáveis curtas (conn → conexao, stmt → statement, rs → resultSet)
- ✅ **Princípio DRY**: Extração de métodos reutilizáveis (construirResultSet, executarQuery)
- ✅ **Funções Pequenas**: Decomposição de métodos grandes em funções com responsabilidades únicas
- ✅ **Tratamento de Erros**: Validações de dados com IllegalArgumentException e melhor propagação de erros
- ✅ **Regra do Escoteiro**: Código deixado mais limpo que o encontrado
- ✅ **Testagem com Spock**: Testes especializados com mocks, setup/cleanup e asserções específicas
- ✅ **Comentários Necessários Only**: Removidos comentários óbvios, mantendo apenas os relevantes

## Funcionalidades Principais

- 📋 Listar empresas cadastradas no banco
- 👥 Listar candidatos com competências
- 🏢 Cadastrar nova empresa
- 🎓 Cadastrar candidato com competências (criação automática de novas competências)
- 💼 Publicar vaga vinculada a empresa e competências
- 🔍 Listar vagas disponíveis com requisitos
- 🛠️ Dicionário de competências do sistema

## Arquitetura

```
src/
├── main/groovy/
│   ├── domain/          # Entidades de domínio
│   │   ├── Pessoa.groovy
│   │   ├── Candidato.groovy
│   │   ├── Empresa.groovy
│   │   ├── Vaga.groovy
│   │   └── Curtida.groovy
│   ├── dao/             # Camada de acesso a dados via JDBC
│   │   ├── CandidatoDAO.groovy
│   │   ├── EmpresaDAO.groovy
│   │   ├── VagaDAO.groovy
│   │   └── CompetenciaDAO.groovy
│   ├── util/
│   │   └── DataBaseConnection.groovy   # Fábrica de conexão
│   └── Linketinder.groovy  # Menu interativo principal
└── test/groovy/
    └── DAOSpec.groovy       # Testes com Spock
```

## Stack Técnica

- **Linguagem**: Groovy 5.0.0
- **Build**: Gradle 9.4 (plugins: groovy, application)
- **Banco de Dados**: PostgreSQL 14+ (driver: 42.7.2)
- **Testes**: Spock Framework 2.4 (com byte-buddy para mocks)
- **JDK**: Java 21+

## Pré-requisitos

- ✅ JDK 21 ou superior
- ✅ Gradle 9.4+ (ou usar ./gradlew)
- ✅ PostgreSQL em execução
- ✅ Banco `db_linketinder` criado
- ✅ Ajustar credenciais em `DataBaseConnection.groovy` se necessário

## Como Executar

### Compilar e Executar
```bash
cd /home/fernando/IdeaProjects/LInLinketinder
./gradlew run
```

### Rodar Testes
```bash
./gradlew test
```

> **Nota**: Os testes usam integração com banco de dados. Certifique-se de que o schema e as tabelas existem previamente.

### Limpar Build
```bash
./gradlew clean
```

## Padrões de Código Aplicados

### DRY (Don't Repeat Yourself)
- Métodos auxiliares extraídos para evitar duplicação
- Exemplo: `construirEmpresaDoResultSet()`, `preencherParametrosEmpresa()`

### Single Responsibility Principle
- Cada método tem uma única responsabilidade clara
- Exemplo: `inserirEmpresa()`, `validarDadosDaEmpresa()`, `listarEmpresas()`

### Tratamento de Erros
- Validações de entrada com exceções customizadas
- Try-catch com mensagens descritivas
- Propagação de erros quando apropriado

### Nomes Descritivos
Exemplos de mudanças:
- `conn` → `conexao`
- `stmt` → `statement`
- `rs` → `resultSet`
- `listarCandidatos()` → mantém clareza
- `buscarOuCriarCompetencia()` → nome autoexplicativo

## Estrutura de Dados

### Tabelas Principais
- `candidatos` - Dados básicos do candidato
- `empresas` - Informações das empresas
- `vagas` - Vagas disponíveis
- `competencias` - Dicionário de competências
- `candidato_competencias` - Relação N:N candidato-competência
- `vaga_competencias` - Relação N:N vaga-competência

## Testes

Os testes em `DAOSpec.groovy` cobrem:
- ✅ Salvar e recuperar empresas
- ✅ Salvar candidatos com habilidades (teste de N:N)
- ✅ Buscar ou criar competências
- ✅ Salvar vagas com competências relacionadas
- ✅ Limpeza correta de dados (cleanup fase)

### Executar Testes Específicos
```bash
./gradlew test --tests DAOSpec
```

## Próximas Melhorias Sugeridas

- 🔐 Implementar autenticação/autorização
- 📡 Criar API REST com Spring Boot
- 🔄 Adicionar testes de integração com Docker
- 🌳 Implementar logging estruturado (SLF4J)
- 💾 Adicionar migrations de banco (Flyway/Liquibase)

## Autor

**Fernando Santos**

---

*Última atualização: 28/04/2026*
