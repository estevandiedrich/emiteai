-- Migration V5: Criação das tabelas principais da aplicação
-- Esta migração garante que todas as tabelas necessárias existam

-- Criar tabela pessoa caso não exista
CREATE TABLE IF NOT EXISTS pessoa (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(50),
    cpf VARCHAR(14) NOT NULL UNIQUE
);

-- Criar tabela endereco caso não exista
CREATE TABLE IF NOT EXISTS endereco (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(50),
    complemento VARCHAR(255),
    cep VARCHAR(10),
    bairro VARCHAR(255),
    municipio VARCHAR(255),
    estado VARCHAR(2),
    pessoa_id BIGINT UNIQUE,
    CONSTRAINT fk_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);

-- Criar tabela auditoria caso não exista
CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    timestamp_requisicao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_http VARCHAR(10) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    ip_origem VARCHAR(45),
    user_agent VARCHAR(1000),
    dados_requisicao TEXT,
    status_resposta INTEGER,
    dados_resposta TEXT,
    tempo_processamento BIGINT,
    erro TEXT,
    usuario_identificado VARCHAR(100)
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_auditoria_timestamp ON auditoria(timestamp_requisicao);
CREATE INDEX IF NOT EXISTS idx_auditoria_endpoint ON auditoria(endpoint);
CREATE INDEX IF NOT EXISTS idx_auditoria_metodo_endpoint ON auditoria(metodo_http, endpoint);
CREATE INDEX IF NOT EXISTS idx_auditoria_status ON auditoria(status_resposta);
CREATE INDEX IF NOT EXISTS idx_auditoria_ip ON auditoria(ip_origem);

-- Comentários
COMMENT ON TABLE pessoa IS 'Tabela principal de pessoas do sistema';
COMMENT ON TABLE endereco IS 'Endereços associados às pessoas';
COMMENT ON TABLE auditoria IS 'Tabela de auditoria para registrar todas as requisições HTTP da API';
