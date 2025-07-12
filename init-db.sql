-- Script de inicialização do banco de dados
-- Este arquivo é executado automaticamente pelo PostgreSQL quando o container é criado

-- Criar o banco de dados emiteai se não existir
SELECT 'CREATE DATABASE emiteai'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'emiteai')\gexec

-- Comentário sobre o banco
COMMENT ON DATABASE emiteai IS 'Banco de dados principal da aplicação EmiteAI - Sistema de auditoria completo';
