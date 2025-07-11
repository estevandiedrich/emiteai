-- Migration inicial para configurar o ambiente
-- Este arquivo é executado antes de qualquer outra migration

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Configurar timezone
SET timezone = 'UTC';

-- Criar schema público caso não exista
CREATE SCHEMA IF NOT EXISTS public;

-- Comentário de inicialização
COMMENT ON SCHEMA public IS 'Schema principal da aplicação EmiteAI';
