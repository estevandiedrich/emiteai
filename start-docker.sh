#!/bin/bash

# Script para inicializar o projeto com Docker
# Este script resolve os problemas de CORS e configura o ambiente corretamente

echo "🚀 Iniciando Projeto EmiteAI com Docker..."

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down

# Reconstruir as imagens sem cache
echo "🔄 Reconstruindo imagens Docker..."
docker-compose build --no-cache

# Iniciar os serviços
echo "🚀 Iniciando serviços..."
docker-compose up -d

# Aguardar alguns segundos para os serviços iniciarem
echo "⏳ Aguardando serviços iniciarem..."
sleep 10

# Verificar status dos containers
echo "✅ Status dos containers:"
docker-compose ps

echo ""
echo "🎉 Projeto iniciado com sucesso!"
echo ""
echo "📋 URLs disponíveis:"
echo "  - Frontend: http://localhost:3000"
echo "  - Backend: http://localhost:8080"
echo "  - PostgreSQL: localhost:5432"
echo "  - RabbitMQ: http://localhost:15672"
echo ""
echo "🔧 Para parar os serviços:"
echo "  docker-compose down"
echo ""
echo "📝 Para ver logs:"
echo "  docker-compose logs -f"
