-- =============================================================
--  Linketinder — Schema completo
--  Inclui tabelas base + suporte à lógica de Match
-- =============================================================

-- -------------------------------------------------------------
--  1. TABELAS BASE
-- -------------------------------------------------------------

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

CREATE TABLE competencias (
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE vagas (
    id         SERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    descricao  TEXT,
    local      VARCHAR(100),
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE
);

CREATE TABLE candidato_competencias (
    candidato_id    INTEGER NOT NULL REFERENCES candidatos(id)   ON DELETE CASCADE,
    competencias_id INTEGER NOT NULL REFERENCES competencias(id) ON DELETE CASCADE,
    PRIMARY KEY (candidato_id, competencias_id)
);

CREATE TABLE vaga_competencias (
    vaga_id        INTEGER NOT NULL REFERENCES vagas(id)        ON DELETE CASCADE,
    competencia_id INTEGER NOT NULL REFERENCES competencias(id) ON DELETE CASCADE,
    PRIMARY KEY (vaga_id, competencia_id)
);

-- -------------------------------------------------------------
--  2. TABELAS DE MATCH
-- -------------------------------------------------------------

-- Registra cada curtida de um candidato em uma vaga.
-- Um candidato pode curtir várias vagas; cada par (candidato, vaga) é único.
CREATE TABLE curtidas_candidato (
    candidato_id INTEGER   NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    vaga_id      INTEGER   NOT NULL REFERENCES vagas(id)      ON DELETE CASCADE,
    data_curtida TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (candidato_id, vaga_id)
);

-- Registra cada curtida de uma empresa em um candidato.
-- Um recruiter pode curtir vários candidatos; cada par (empresa, candidato) é único.
CREATE TABLE curtidas_empresa (
    empresa_id   INTEGER   NOT NULL REFERENCES empresas(id)   ON DELETE CASCADE,
    candidato_id INTEGER   NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    data_curtida TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (empresa_id, candidato_id)
);

-- Registra o match confirmado entre candidato e empresa.
-- O vaga_id aponta para a vaga específica que originou o match
-- (a vaga curtida pelo candidato que pertence à empresa que o curtiu de volta).
-- A constraint UNIQUE garante no máximo um match por par candidato–empresa.
CREATE TABLE matches (
    id           SERIAL    PRIMARY KEY,
    candidato_id INTEGER   NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    empresa_id   INTEGER   NOT NULL REFERENCES empresas(id)   ON DELETE CASCADE,
    vaga_id      INTEGER   NOT NULL REFERENCES vagas(id)      ON DELETE CASCADE,
    data_match   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_match_candidato_empresa UNIQUE (candidato_id, empresa_id)
);

-- -------------------------------------------------------------
--  3. ÍNDICES
-- -------------------------------------------------------------

-- Acelera o rastreio "candidato curtiu alguma vaga desta empresa?"
CREATE INDEX idx_curtidas_candidato_candidato ON curtidas_candidato(candidato_id);
CREATE INDEX idx_curtidas_candidato_vaga      ON curtidas_candidato(vaga_id);

-- Acelera a busca de curtidas da empresa por candidato
CREATE INDEX idx_curtidas_empresa_empresa     ON curtidas_empresa(empresa_id);
CREATE INDEX idx_curtidas_empresa_candidato   ON curtidas_empresa(candidato_id);

-- Acelera consultas de matches por candidato ou empresa
CREATE INDEX idx_matches_candidato ON matches(candidato_id);
CREATE INDEX idx_matches_empresa   ON matches(empresa_id);
