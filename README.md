# Linketinder (MVP) - ZG-Hero

Projeto backend em **Groovy** para simular um sistema de recrutamento no estilo "match", com execução via terminal e persistência em banco de dados.

## O que foi adicionado nos 3 ultimos commits

- **`14c8142`**: atualização das classes de domínio para o contexto de banco e inclusão da entidade `Vaga`.
- **`a8b0e4e`**: implementação de persistência com **JDBC puro** usando DAOs (`CandidatoDAO`, `EmpresaDAO`, `VagaDAO`, `CompetenciaDAO`) e classe de conexão (`DataBaseConnection`).
- **`d83f5ac`**: refatoração do menu principal (`Linketinder.groovy`) para operar totalmente com dados persistidos no banco.

## Funcionalidades atuais

- Listar empresas cadastradas no banco.
- Listar candidatos e suas competências.
- Cadastrar nova empresa.
- Cadastrar novo candidato com competências (criando competências inexistentes quando necessário).
- Publicar vaga vinculada a empresa e competências.
- Listar vagas disponíveis com requisitos.
- Exibir dicionário de competências cadastradas.

## Arquitetura

- `src/main/groovy/domain`: entidades de domínio (`Pessoa`, `Candidato`, `Empresa`, `Vaga`).
- `src/main/groovy/dao`: camada de acesso a dados via JDBC.
- `src/main/groovy/util/DataBaseConnection.groovy`: fábrica de conexão com PostgreSQL.
- `src/main/groovy/domain/Linketinder.groovy`: ponto de entrada da aplicação (menu interativo).

## Stack tecnica

- Groovy 4
- Gradle (plugins `groovy` e `application`)
- PostgreSQL (driver `org.postgresql:postgresql:42.7.2`)
- Spock Framework para testes

## Pre-requisitos

- JDK instalado.
- PostgreSQL em execução.
- Banco `db_linketinder` criado.
- Ajustar credenciais em `src/main/groovy/util/DataBaseConnection.groovy` se necessario.

## Como executar

```bash
cd /home/fernando/IdeaProjects/LInLinketinder
./gradlew run
```

## Como rodar testes

```bash
cd /home/fernando/IdeaProjects/LInLinketinder
./gradlew test
```

> Observação: os testes em `src/test/groovy` usam integração com banco, então o schema/tabelas precisam existir previamente.

## Autor

**Fernando Santos**
