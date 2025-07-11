#!/bin/bash
set -e

# Script simplificado para criar apenas o database
# O Flyway cuidará de todo o resto

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Criar o database 'emiteai' caso não exista
    SELECT 'CREATE DATABASE emiteai'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'emiteai')
    \\gexec
    
    -- Conceder permissões
    GRANT ALL PRIVILEGES ON DATABASE emiteai TO root;
EOSQL

echo "✅ Database 'emiteai' criado com sucesso!"
echo "🔄 Flyway cuidará das migrações e estrutura do schema"
