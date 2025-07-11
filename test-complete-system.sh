#!/bin/bash

# Test script to validate the complete EmiteAI system
# This script tests all the main functionality and audit systems

echo "🔍 Testing Complete EmiteAI System..."
echo "======================================="

# Test 1: API Health Check
echo "✅ Test 1: API Health Check"
API_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/pessoas)
if [ "$API_HEALTH" = "200" ]; then
    echo "   ✅ API is responding (HTTP 200)"
else
    echo "   ❌ API is not responding (HTTP $API_HEALTH)"
    exit 1
fi

# Test 2: Database Connection
echo "✅ Test 2: Database Connection"
TABLES=$(docker exec -it emiteai_project_complete_postgres_1 psql -U root -d emiteai -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>/dev/null | tr -d '[:space:]')
if [ "$TABLES" -ge "6" ]; then
    echo "   ✅ Database tables exist ($TABLES tables found)"
else
    echo "   ❌ Database tables missing ($TABLES tables found)"
    exit 1
fi

# Test 3: Create Person (POST)
echo "✅ Test 3: Create Person (POST)"
CREATE_RESULT=$(curl -s -X POST http://localhost:8080/api/pessoas \
  -H "Content-Type: application/json" \
  -d '{"nome": "Test System", "telefone": "(11) 12345-6789", "cpf": "999.888.777-66", "endereco": {"numero": "456", "cep": "12345-678", "bairro": "Test District", "municipio": "Test City", "estado": "SP"}}')

if echo "$CREATE_RESULT" | grep -q "Test System"; then
    echo "   ✅ Person created successfully"
    PERSON_ID=$(echo "$CREATE_RESULT" | grep -o '"id":[0-9]*' | sed 's/"id"://')
    echo "   📝 Created person with ID: $PERSON_ID"
else
    echo "   ❌ Failed to create person"
    exit 1
fi

# Test 4: Update Person (PUT)
echo "✅ Test 4: Update Person (PUT)"
UPDATE_RESULT=$(curl -s -X PUT http://localhost:8080/api/pessoas/$PERSON_ID \
  -H "Content-Type: application/json" \
  -d '{"nome": "Test System Updated", "telefone": "(11) 98765-4321", "cpf": "999.888.777-66"}')

if echo "$UPDATE_RESULT" | grep -q "Test System Updated"; then
    echo "   ✅ Person updated successfully"
else
    echo "   ❌ Failed to update person"
    exit 1
fi

# Test 5: HTTP Audit System
echo "✅ Test 5: HTTP Audit System"
HTTP_AUDIT_COUNT=$(docker exec -it emiteai_project_complete_postgres_1 psql -U root -d emiteai -t -c "SELECT COUNT(*) FROM auditoria;" 2>/dev/null | tr -d '[:space:]')
if [ "$HTTP_AUDIT_COUNT" -gt "0" ]; then
    echo "   ✅ HTTP audit records exist ($HTTP_AUDIT_COUNT records)"
else
    echo "   ❌ HTTP audit records missing"
    exit 1
fi

# Test 6: Envers Audit System
echo "✅ Test 6: Envers Audit System"
ENVERS_AUDIT_COUNT=$(docker exec -it emiteai_project_complete_postgres_1 psql -U root -d emiteai -t -c "SELECT COUNT(*) FROM pessoa_aud;" 2>/dev/null | tr -d '[:space:]')
if [ "$ENVERS_AUDIT_COUNT" -gt "0" ]; then
    echo "   ✅ Envers audit records exist ($ENVERS_AUDIT_COUNT records)"
else
    echo "   ❌ Envers audit records missing"
    exit 1
fi

# Test 7: Envers Revision System
echo "✅ Test 7: Envers Revision System"
REVISION_COUNT=$(docker exec -it emiteai_project_complete_postgres_1 psql -U root -d emiteai -t -c "SELECT COUNT(*) FROM revinfo;" 2>/dev/null | tr -d '[:space:]')
if [ "$REVISION_COUNT" -gt "0" ]; then
    echo "   ✅ Envers revision records exist ($REVISION_COUNT revisions)"
else
    echo "   ❌ Envers revision records missing"
    exit 1
fi

# Test 8: Frontend Access
echo "✅ Test 8: Frontend Access"
FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000)
if [ "$FRONTEND_STATUS" = "200" ]; then
    echo "   ✅ Frontend is accessible (HTTP 200)"
else
    echo "   ❌ Frontend is not accessible (HTTP $FRONTEND_STATUS)"
    exit 1
fi

# Test 9: RabbitMQ Connection
echo "✅ Test 9: RabbitMQ Connection"
RABBITMQ_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:15672)
if [ "$RABBITMQ_STATUS" = "200" ]; then
    echo "   ✅ RabbitMQ Management is accessible (HTTP 200)"
else
    echo "   ❌ RabbitMQ Management is not accessible (HTTP $RABBITMQ_STATUS)"
    exit 1
fi

echo "======================================="
echo "🎉 ALL TESTS PASSED! System is fully functional."
echo ""
echo "📊 System Summary:"
echo "   - API: ✅ Working (http://localhost:8080)"
echo "   - Frontend: ✅ Working (http://localhost:3000)"
echo "   - Database: ✅ Working (PostgreSQL on port 5432)"
echo "   - RabbitMQ: ✅ Working (port 5672, management on 15672)"
echo "   - HTTP Audit: ✅ Working ($HTTP_AUDIT_COUNT records)"
echo "   - Envers Audit: ✅ Working ($ENVERS_AUDIT_COUNT records, $REVISION_COUNT revisions)"
echo ""
echo "🔗 Access URLs:"
echo "   - Frontend: http://localhost:3000"
echo "   - API: http://localhost:8080/api/pessoas"
echo "   - RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo ""
echo "📋 Database Tables:"
echo "   - pessoa (main entity)"
echo "   - endereco (address entity)"
echo "   - auditoria (HTTP audit log)"
echo "   - pessoa_aud (Envers audit for pessoa)"
echo "   - endereco_aud (Envers audit for endereco)"
echo "   - revinfo (Envers revision info)"
