#!/bin/bash

# Script para testar a criação automática de databases
echo "🧪 Testando criação automática de databases..."

# Parar containers se estiverem rodando
echo "🛑 Parando containers existentes..."
docker-compose down

# Remover volumes para garantir teste limpo
echo "🧹 Removendo volumes antigos..."
docker volume prune -f

# Iniciar apenas o PostgreSQL
echo "🚀 Iniciando PostgreSQL..."
docker-compose up -d postgres

# Aguardar PostgreSQL inicializar
echo "⏳ Aguardando PostgreSQL inicializar..."
sleep 10

# Verificar se os databases foram criados
echo "🔍 Verificando databases criados..."
docker-compose exec postgres psql -U root -d postgres -c "\l"

# Verificar especificamente os databases que queremos
echo "✅ Verificando database 'emiteai'..."
docker-compose exec postgres psql -U root -d postgres -c "SELECT datname FROM pg_database WHERE datname = 'emiteai';"

echo "✅ Verificando database 'inventory-db'..."
docker-compose exec postgres psql -U root -d postgres -c "SELECT datname FROM pg_database WHERE datname = 'inventory-db';"

# Iniciar o resto da aplicação
echo "🚀 Iniciando aplicação completa..."
docker-compose up -d

echo "✅ Teste concluído! Verifique os logs acima para confirmar que os databases foram criados."
