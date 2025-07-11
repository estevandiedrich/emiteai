-- Criação da tabela de auditoria
CREATE TABLE auditoria (
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

-- Índices para melhorar performance das consultas
CREATE INDEX idx_auditoria_timestamp ON auditoria(timestamp_requisicao);
CREATE INDEX idx_auditoria_endpoint ON auditoria(endpoint);
CREATE INDEX idx_auditoria_metodo_endpoint ON auditoria(metodo_http, endpoint);
CREATE INDEX idx_auditoria_status ON auditoria(status_resposta);
CREATE INDEX idx_auditoria_ip ON auditoria(ip_origem);

-- Comentários na tabela
COMMENT ON TABLE auditoria IS 'Tabela de auditoria para registrar todas as requisições HTTP da API';
COMMENT ON COLUMN auditoria.timestamp_requisicao IS 'Data e hora da requisição';
COMMENT ON COLUMN auditoria.metodo_http IS 'Método HTTP (GET, POST, PUT, DELETE, etc.)';
COMMENT ON COLUMN auditoria.endpoint IS 'Endpoint acessado';
COMMENT ON COLUMN auditoria.ip_origem IS 'IP de origem da requisição';
COMMENT ON COLUMN auditoria.user_agent IS 'User agent do cliente';
COMMENT ON COLUMN auditoria.dados_requisicao IS 'Dados enviados na requisição (JSON)';
COMMENT ON COLUMN auditoria.status_resposta IS 'Status HTTP da resposta';
COMMENT ON COLUMN auditoria.dados_resposta IS 'Dados retornados na resposta (JSON)';
COMMENT ON COLUMN auditoria.tempo_processamento IS 'Tempo de processamento em milissegundos';
COMMENT ON COLUMN auditoria.erro IS 'Informações de erro se houver';
COMMENT ON COLUMN auditoria.usuario_identificado IS 'Usuário identificado (para futuras implementações de autenticação)';
