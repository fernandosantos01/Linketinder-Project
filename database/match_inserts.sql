-- =============================================================
--  Linketinder — Dados de demonstração da lógica de Match
--  Execute após schema.sql
-- =============================================================

-- -------------------------------------------------------------
--  1. COMPETÊNCIAS
-- -------------------------------------------------------------

INSERT INTO competencias (nome) VALUES
    ('Java'),
    ('Groovy'),
    ('SQL'),
    ('Python'),
    ('JavaScript'),
    ('Docker'),
    ('Spring Boot'),
    ('React');

-- -------------------------------------------------------------
--  2. CANDIDATOS
-- -------------------------------------------------------------

INSERT INTO candidatos (nome, data_nascimento, email, cpf, pais, estado, cep, descricao) VALUES
    ('Sandubinha',    '1995-03-10', 'sandubinha@email.com',  '111.222.333-44', 'Brasil', 'SP', '01310-100', 'Dev apaixonado por backend, buscando oportunidade para crescer.'),
    ('Maria Silva',   '1992-07-22', 'maria.silva@email.com', '222.333.444-55', 'Brasil', 'RJ', '20040-020', 'Especialista em dados e automação com Python.'),
    ('Carlos Rocha',  '1998-11-05', 'carlos.rocha@email.com','333.444.555-66', 'Brasil', 'MG', '30112-010', 'Frontend developer com foco em React e UX.');

-- Competências dos candidatos
INSERT INTO candidato_competencias (candidato_id, competencias_id) VALUES
    -- Sandubinha: Java, Groovy, SQL
    (1, 1), (1, 2), (1, 3),
    -- Maria: Python, SQL, Docker
    (2, 4), (2, 3), (2, 6),
    -- Carlos: JavaScript, React
    (3, 5), (3, 8);

-- -------------------------------------------------------------
--  3. EMPRESAS
-- -------------------------------------------------------------

INSERT INTO empresas (nome, cnpj, email, descricao, pais, estado, cep) VALUES
    ('TrollSoft',    '11.222.333/0001-44', 'rh@trollsoft.com',   'Startup de desenvolvimento de software enterprise.',          'Brasil', 'SP', '04538-132'),
    ('DataCorp',     '22.333.444/0001-55', 'jobs@datacorp.com',  'Empresa de análise e engenharia de dados.',                  'Brasil', 'RJ', '22250-040'),
    ('WebFront Inc', '33.444.555/0001-66', 'talentos@webfront.com', 'Agência especializada em interfaces web e experiência do usuário.', 'Brasil', 'SP', '01452-001');

-- -------------------------------------------------------------
--  4. VAGAS
-- -------------------------------------------------------------

INSERT INTO vagas (nome, descricao, local, empresa_id) VALUES
    ('Dev Backend Java',     'Vaga para desenvolvedor backend com foco em microsserviços.',    'São Paulo - SP', 1),
    ('Dev Groovy Sênior',    'Desenvolvimento de sistemas internos em Groovy/Grails.',          'Remoto',         1),
    ('Engenheiro de Dados',  'Pipeline de dados e ETL com Python e SQL.',                      'Rio de Janeiro', 2),
    ('Dev Python Júnior',    'Automação e scripts de análise de dados.',                        'Remoto',         2),
    ('Frontend React',       'Desenvolvimento de interfaces web modernas com React.',           'São Paulo - SP', 3);

-- Competências das vagas
INSERT INTO vaga_competencias (vaga_id, competencia_id) VALUES
    -- Vaga 1 (Dev Backend Java): Java, SQL, Spring Boot, Docker
    (1, 1), (1, 3), (1, 7), (1, 6),
    -- Vaga 2 (Dev Groovy Sênior): Groovy, Java, SQL
    (2, 2), (2, 1), (2, 3),
    -- Vaga 3 (Engenheiro de Dados): Python, SQL, Docker
    (3, 4), (3, 3), (3, 6),
    -- Vaga 4 (Dev Python Júnior): Python, SQL
    (4, 4), (4, 3),
    -- Vaga 5 (Frontend React): JavaScript, React
    (5, 5), (5, 8);

-- -------------------------------------------------------------
--  5. CURTIDAS DOS CANDIDATOS
-- -------------------------------------------------------------

-- Sandubinha curte a vaga de Dev Groovy Sênior da TrollSoft
-- (caso do enunciado: ele vê que a vaga exige mais do que tem, mas curte mesmo assim)
INSERT INTO curtidas_candidato (candidato_id, vaga_id, data_curtida) VALUES
    (1, 2, '2024-06-10 09:15:00');

-- Sandubinha também curte a vaga de Dev Backend Java (mesma empresa)
INSERT INTO curtidas_candidato (candidato_id, vaga_id, data_curtida) VALUES
    (1, 1, '2024-06-10 09:22:00');

-- Maria curte a vaga de Engenheiro de Dados da DataCorp
INSERT INTO curtidas_candidato (candidato_id, vaga_id, data_curtida) VALUES
    (2, 3, '2024-06-11 14:00:00');

-- Carlos curte a vaga de Frontend React da WebFront Inc
INSERT INTO curtidas_candidato (candidato_id, vaga_id, data_curtida) VALUES
    (3, 5, '2024-06-12 10:30:00');

-- Maria também curte a vaga de Dev Python Júnior da DataCorp
INSERT INTO curtidas_candidato (candidato_id, vaga_id, data_curtida) VALUES
    (2, 4, '2024-06-12 15:45:00');

-- -------------------------------------------------------------
--  6. CURTIDAS DAS EMPRESAS
-- -------------------------------------------------------------

-- TrollSoft curte Sandubinha — rastreio encontra que ele curtiu vagas da TrollSoft → MATCH
INSERT INTO curtidas_empresa (empresa_id, candidato_id, data_curtida) VALUES
    (1, 1, '2024-06-10 18:00:00');

-- DataCorp curte Maria — rastreio encontra que ela curtiu vagas da DataCorp → MATCH
INSERT INTO curtidas_empresa (empresa_id, candidato_id, data_curtida) VALUES
    (2, 2, '2024-06-12 16:30:00');

-- WebFront Inc curte Carlos — rastreio encontra que ele curtiu a vaga deles → MATCH
INSERT INTO curtidas_empresa (empresa_id, candidato_id, data_curtida) VALUES
    (3, 3, '2024-06-13 11:00:00');

-- TrollSoft curte Maria — Maria NÃO curtiu nenhuma vaga da TrollSoft → sem match
INSERT INTO curtidas_empresa (empresa_id, candidato_id, data_curtida) VALUES
    (1, 2, '2024-06-13 09:00:00');

-- -------------------------------------------------------------
--  7. MATCHES CONFIRMADOS
--  Inseridos após o rastreio identificar curtida recíproca.
--  vaga_id = primeira vaga da empresa curtida pelo candidato.
-- -------------------------------------------------------------

-- Match: Sandubinha ↔ TrollSoft (via vaga "Dev Groovy Sênior", curtida primeiro)
INSERT INTO matches (candidato_id, empresa_id, vaga_id, data_match) VALUES
    (1, 1, 2, '2024-06-10 18:00:01');

-- Match: Maria ↔ DataCorp (via vaga "Engenheiro de Dados", curtida primeiro)
INSERT INTO matches (candidato_id, empresa_id, vaga_id, data_match) VALUES
    (2, 2, 3, '2024-06-12 16:30:01');

-- Match: Carlos ↔ WebFront Inc (via vaga "Frontend React")
INSERT INTO matches (candidato_id, empresa_id, vaga_id, data_match) VALUES
    (3, 3, 5, '2024-06-13 11:00:01');

-- -------------------------------------------------------------
--  8. QUERY DE RASTREIO — demonstração da lógica de match
--
--  Simula o que a aplicação executa quando uma empresa curte
--  um candidato: verifica se o candidato curtiu alguma vaga
--  desta empresa e, se sim, registra o match.
--
--  Exemplo: verificar se Sandubinha (id=1) curtiu alguma vaga
--           da TrollSoft (empresa_id=1).
-- -------------------------------------------------------------

/*
SELECT cc.candidato_id,
       cc.vaga_id,
       v.nome        AS vaga_nome,
       v.empresa_id
FROM curtidas_candidato cc
JOIN vagas v ON v.id = cc.vaga_id
WHERE cc.candidato_id = 1
  AND v.empresa_id    = 1
LIMIT 1;
*/

-- -------------------------------------------------------------
--  9. QUERY DE LISTAGEM DE MATCHES — visão consolidada
-- -------------------------------------------------------------

/*
SELECT
    m.id                AS match_id,
    c.nome              AS candidato,
    e.nome              AS empresa,
    v.nome              AS vaga_origem,
    m.data_match
FROM matches m
JOIN candidatos c ON c.id = m.candidato_id
JOIN empresas   e ON e.id = m.empresa_id
JOIN vagas      v ON v.id = m.vaga_id
ORDER BY m.data_match;
*/
