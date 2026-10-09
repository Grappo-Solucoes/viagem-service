-- Schema próprio do viagem-service (decisão D7): só tabelas deste contexto,
-- sem FKs para fora. Tipos espelham o mapeamento JPA (validate).
CREATE SCHEMA IF NOT EXISTS viagem;

CREATE TABLE IF NOT EXISTS viagem.rotas (
    id VARCHAR(255) NOT NULL,
    version BIGINT,
    distancia VARCHAR(255),
    nome VARCHAR(255),
    numero_rota VARCHAR(255) NOT NULL,
    rotas_json TEXT,
    tempo_estimado VARCHAR(255),
    tenant_id VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS viagem.rota_enderecos (
    rota_id VARCHAR(255) NOT NULL,
    numero INTEGER,
    bairro VARCHAR(255),
    cep VARCHAR(255),
    cidade VARCHAR(255),
    complemento VARCHAR(255),
    estado VARCHAR(255),
    latitude VARCHAR(255),
    logradouro VARCHAR(255),
    longitude VARCHAR(255),
    CONSTRAINT fk_rota_enderecos_rota FOREIGN KEY (rota_id) REFERENCES viagem.rotas (id)
);

CREATE TABLE IF NOT EXISTS viagem.viagens_execucao (
    id VARCHAR(255) NOT NULL,
    version BIGINT,
    atraso_acumulado BIGINT,
    data TIMESTAMP(6) NOT NULL,
    data_fim TIMESTAMP(6),
    data_inicio TIMESTAMP(6),
    horario_previsto_fim TIMESTAMP(6),
    horario_previsto_inicio TIMESTAMP(6),
    horario_real_fim TIMESTAMP(6),
    horario_real_inicio TIMESTAMP(6),
    monitor_id UUID NOT NULL,
    motorista_id UUID NOT NULL,
    planejamento_id UUID,
    veiculo_id UUID NOT NULL,
    codigo_viagem VARCHAR(255),
    rota_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_viagem_status CHECK (status IN ('PLANEJADA','INICIADA','PAUSADA','FINALIZADA','CANCELADA'))
);

CREATE TABLE IF NOT EXISTS viagem.viagem_passageiros (
    viagem_id VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL,
    passageiro_id UUID NOT NULL,
    escola VARCHAR(255),
    matricula VARCHAR(255),
    nome VARCHAR(255),
    ponto_embarque VARCHAR(255),
    turno VARCHAR(255),
    CONSTRAINT fk_viagem_passageiros_viagem FOREIGN KEY (viagem_id) REFERENCES viagem.viagens_execucao (id)
);

CREATE TABLE IF NOT EXISTS viagem.viagem_embarques (
    viagem_id VARCHAR(255) NOT NULL,
    latitude_desembarque DOUBLE PRECISION,
    latitude_embarque DOUBLE PRECISION,
    longitude_desembarque DOUBLE PRECISION,
    longitude_embarque DOUBLE PRECISION,
    planejado BOOLEAN NOT NULL,
    desembarcado_em TIMESTAMP(6),
    embarcado_em TIMESTAMP(6),
    passageiro_id UUID NOT NULL,
    justificativa VARCHAR(500),
    nome VARCHAR(255),
    CONSTRAINT fk_viagem_embarques_viagem FOREIGN KEY (viagem_id) REFERENCES viagem.viagens_execucao (id)
);

CREATE TABLE IF NOT EXISTS viagem.viagem_paradas (
    id VARCHAR(255) NOT NULL,
    version BIGINT,
    ordem INTEGER NOT NULL,
    ordem_execucao INTEGER,
    horario_chegada_real TIMESTAMP(6),
    horario_previsto TIMESTAMP(6),
    horario_saida_real TIMESTAMP(6),
    latitude_chegada DOUBLE PRECISION,
    longitude_chegada DOUBLE PRECISION,
    latitude_saida DOUBLE PRECISION,
    longitude_saida DOUBLE PRECISION,
    tenant_id VARCHAR(255),
    viagem_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_viagem_paradas_viagem FOREIGN KEY (viagem_id) REFERENCES viagem.viagens_execucao (id),
    CONSTRAINT ck_parada_status CHECK (status IN ('PENDENTE','APROXIMANDO','CHEGADA_REGISTRADA','CONCLUIDA','PULADA'))
);
