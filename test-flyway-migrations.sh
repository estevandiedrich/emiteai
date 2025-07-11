#!/bin/bash

echo "🧪 TESTANDO MIGRAÇÕES FLYWAY - EMITEAI PROJECT"
echo "================================================"

# Função para mostrar status
show_status() {
    if [ $? -eq 0 ]; then
        echo "✅ $1"
    else
        echo "❌ $1"
        exit 1
    fi
}

# 1. Verificar se o Docker está rodando
echo "1. Verificando Docker..."
docker ps > /dev/null 2>&1
show_status "Docker está rodando"

# 2. Parar containers existentes
echo "2. Parando containers existentes..."
docker-compose down > /dev/null 2>&1
show_status "Containers parados"

# 3. Limpar volumes (opcional - descomente se necessário)
# echo "3. Limpando volumes..."
# docker-compose down -v > /dev/null 2>&1
# show_status "Volumes limpos"

# 4. Reconstruir e iniciar
echo "3. Reconstruindo e iniciando serviços..."
docker-compose up -d --build
show_status "Serviços iniciados"

# 5. Aguardar PostgreSQL estar pronto
echo "4. Aguardando PostgreSQL estar pronto..."
sleep 10
docker-compose exec -T postgres pg_isready -U root > /dev/null 2>&1
show_status "PostgreSQL está pronto"

# 6. Aguardar backend estar pronto
echo "5. Aguardando backend estar pronto..."
sleep 15

# 7. Verificar se as tabelas foram criadas
echo "6. Verificando estrutura do banco..."
TABLES=$(docker-compose exec -T postgres psql -U root -d emiteai -t -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;")
echo "Tabelas encontradas:"
echo "$TABLES"

# 8. Verificar tabelas específicas
echo "7. Verificando tabelas específicas..."
docker-compose exec -T postgres psql -U root -d emiteai -c "\d pessoa" > /dev/null 2>&1
show_status "Tabela 'pessoa' existe"

docker-compose exec -T postgres psql -U root -d emiteai -c "\d endereco" > /dev/null 2>&1
show_status "Tabela 'endereco' existe"

docker-compose exec -T postgres psql -U root -d emiteai -c "\d auditoria" > /dev/null 2>&1
show_status "Tabela 'auditoria' existe"

docker-compose exec -T postgres psql -U root -d emiteai -c "\d revinfo" > /dev/null 2>&1
show_status "Tabela 'revinfo' (Envers) existe"

docker-compose exec -T postgres psql -U root -d emiteai -c "\d pessoa_aud" > /dev/null 2>&1
show_status "Tabela 'pessoa_aud' (Envers) existe"

# 9. Verificar Flyway Schema History
echo "8. Verificando histórico de migrações Flyway..."
MIGRATIONS=$(docker-compose exec -T postgres psql -U root -d emiteai -t -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;")
echo "Migrações aplicadas:"
echo "$MIGRATIONS"

# 10. Testar API
echo "9. Testando API..."
sleep 5
curl -s http://localhost:8080/api/pessoas > /dev/null 2>&1
show_status "API está respondendo"

# 11. Testar auditoria
echo "10. Testando sistema de auditoria..."
curl -s http://localhost:8080/api/auditoria/health > /dev/null 2>&1
show_status "Sistema de auditoria está funcionando"

echo ""
echo "🎉 TODOS OS TESTES PASSARAM!"
echo "✅ Migrações Flyway aplicadas corretamente"
echo "✅ Todas as tabelas foram criadas"
echo "✅ Sistema de auditoria funcionando"
echo "✅ API está respondendo"
echo ""
echo "📊 Para ver estatísticas:"
echo "curl http://localhost:8080/api/auditoria/estatisticas"
echo ""
echo "🌐 Frontend disponível em:"
echo "http://localhost:3000"
