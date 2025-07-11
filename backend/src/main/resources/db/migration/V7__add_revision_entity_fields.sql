-- Migration para adicionar campos avançados à tabela revinfo do Envers
-- V7__add_revision_entity_fields.sql

-- Adicionar campos para capturar informações do contexto da requisição
ALTER TABLE revinfo ADD COLUMN IF NOT EXISTS usuario VARCHAR(100);
ALTER TABLE revinfo ADD COLUMN IF NOT EXISTS ip_origem VARCHAR(45);
ALTER TABLE revinfo ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);
ALTER TABLE revinfo ADD COLUMN IF NOT EXISTS motivo VARCHAR(500);

-- Adicionar comentários para documentação
COMMENT ON COLUMN revinfo.usuario IS 'Usuário que realizou a modificação';
COMMENT ON COLUMN revinfo.ip_origem IS 'IP de origem da requisição';
COMMENT ON COLUMN revinfo.user_agent IS 'User-Agent do cliente';
COMMENT ON COLUMN revinfo.motivo IS 'Motivo da modificação (opcional)';
