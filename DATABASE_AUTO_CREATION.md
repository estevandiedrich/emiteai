# 🗄️ Criação Automática de Databases com Flyway

## 📋 Visão Geral

O projeto agora inclui criação automática de databases usando múltiplas estratégias para garantir máxima compatibilidade:

1. **Script de Inicialização Docker** (`init-db.sh`)
2. **Configuração Spring Boot** (`DatabaseInitializer.java`)
3. **Configuração Flyway** (`FlywayConfig.java`)
4. **Migrations Flyway** (`V0__create_database.sql`)

## 🔧 Implementações

### 1. Script de Inicialização Docker (`init-db.sh`)
```bash
#!/bin/bash
# Executado quando o container PostgreSQL é iniciado
# Cria databases automaticamente
```

### 2. Componente Spring (`DatabaseInitializer.java`)
```java
@Component
@Profile("!test")
public class DatabaseInitializer implements CommandLineRunner {
    // Verifica e cria databases na inicialização da aplicação
}
```

### 3. Configuração Flyway (`FlywayConfig.java`)
```java
@Configuration
@Profile("!test")
public class FlywayConfig {
    // Configura Flyway para múltiplos databases
}
```

### 4. Configuração application.yml
```yaml
spring:
  flyway:
    enabled: true
    create-schemas: true
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
```

## 🗄️ Databases Criados

### Automaticamente:
- **emiteai** - Database principal da aplicação
- **inventory-db** - Database adicional (se necessário)

## 🚀 Como Usar

### Método 1: Script Automatizado
```bash
./start-docker.sh
```

### Método 2: Teste Específico
```bash
./test-database-creation.sh
```

### Método 3: Manual
```bash
docker-compose up -d
```

## 🔍 Verificação

Para verificar se os databases foram criados:

```bash
# Listar todos os databases
docker-compose exec postgres psql -U root -d postgres -c "\l"

# Verificar database específico
docker-compose exec postgres psql -U root -d postgres -c "SELECT datname FROM pg_database WHERE datname = 'emiteai';"
```

## 📊 Logs da Aplicação

A aplicação mostra mensagens no log indicando o status:
- ✅ Database criado com sucesso
- ℹ️  Database já existe
- ❌ Erro ao criar database

## 🔧 Configurações Avançadas

### Flyway Features Habilitadas:
- `create-schemas: true` - Cria schemas automaticamente
- `baseline-on-migrate: true` - Permite migração em databases existentes
- `baseline-version: 0` - Versão inicial para baseline
- `validate-on-migrate: true` - Valida migrations antes de aplicar

### Perfis de Execução:
- **Desenvolvimento**: Todos os recursos habilitados
- **Docker**: Todos os recursos habilitados
- **Teste**: DatabaseInitializer desabilitado (usa H2)

## 🎯 Benefícios

1. **Automação Completa** - Sem configuração manual
2. **Múltiplas Estratégias** - Funciona em diferentes cenários
3. **Tolerância a Falhas** - Não falha se database já existir
4. **Flexibilidade** - Suporta múltiplos databases
5. **Integração Docker** - Funciona perfeitamente com containers

## 🧪 Testes

Execute o script de teste para verificar:
```bash
./test-database-creation.sh
```

Este script:
1. Para containers existentes
2. Remove volumes antigos
3. Inicia PostgreSQL
4. Verifica criação dos databases
5. Inicia aplicação completa

---

**Autor**: Sistema EmiteAI  
**Data**: 11 de Julho de 2025  
**Status**: ✅ Implementado e Testado
