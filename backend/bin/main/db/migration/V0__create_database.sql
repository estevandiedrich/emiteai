-- Migration inicial para configurar o ambiente completo
-- Este arquivo é executado antes de qualquer outra migration

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Configurar timezone
SET timezone = 'UTC';

-- Criar schema público caso não exista
CREATE SCHEMA IF NOT EXISTS public;

-- Criar database emiteai caso não exista (apenas documentação, o Flyway não executa isso)
-- O database deve ser criado externamente ou via init script

-- Configurações de performance para auditoria
SET log_statement = 'none'; -- Evitar logs excessivos durante desenvolvimento

-- Comentário de inicialização
COMMENT ON SCHEMA public IS 'Schema principal da aplicação EmiteAI - Sistema de auditoria completo';
