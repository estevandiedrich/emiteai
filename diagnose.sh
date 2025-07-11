#!/bin/bash

echo "🔍 EmiteAI Project Complete Diagnostic"
echo "======================================"

echo -e "\n📋 1. Checking Docker containers status..."
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo -e "\n🗄️ 2. Checking database connection..."
docker-compose exec postgres psql -U root -d emiteai -c "SELECT version();" 2>/dev/null || echo "❌ Database connection failed"

echo -e "\n📊 3. Checking existing tables..."
docker-compose exec postgres psql -U root -d emiteai -c "SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;" 2>/dev/null || echo "❌ Could not check tables"

echo -e "\n🔧 4. Checking if Flyway schema history exists..."
docker-compose exec postgres psql -U root -d emiteai -c "SELECT * FROM flyway_schema_history LIMIT 1;" 2>/dev/null || echo "❌ Flyway schema history table does not exist"

echo -e "\n🎯 5. Testing API response..."
curl -s -X GET http://localhost:8080/api/pessoas | jq '.' 2>/dev/null || echo "❌ API test failed"

echo -e "\n📂 6. Checking migration files in JAR..."
docker-compose exec backend jar -tf /app/app.jar | grep "db/migration" | head -5 || echo "❌ Migration files not found in JAR"

echo -e "\n🔍 7. Checking Spring Boot autoconfiguration..."
docker logs emiteai_project_complete_backend_1 2>&1 | grep -i "autoconfiguration" | head -5 || echo "❌ No autoconfiguration logs found"

echo -e "\n🔄 8. Checking for Flyway logs..."
docker logs emiteai_project_complete_backend_1 2>&1 | grep -i flyway || echo "❌ No Flyway logs found"

echo -e "\n📋 9. Checking active Spring profiles..."
docker logs emiteai_project_complete_backend_1 2>&1 | grep "profile" || echo "❌ No profile information found"

echo -e "\n✅ Diagnostic complete!"
