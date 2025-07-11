#!/bin/bash
set -e

# Script para criar o database caso não exista
# Este script é executado quando o container PostgreSQL é iniciado

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Criar o database 'emiteai' caso não exista
    SELECT 'CREATE DATABASE emiteai'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'emiteai')
    \gexec
    
    -- Criar o database 'inventory-db' caso não exista (se for necessário)
    SELECT 'CREATE DATABASE "inventory-db"'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inventory-db')
    \gexec
    
    -- Conceder permissões
    GRANT ALL PRIVILEGES ON DATABASE emiteai TO root;
    GRANT ALL PRIVILEGES ON DATABASE "inventory-db" TO root;
EOSQL

echo "Databases criados com sucesso!"
