# Feature Development Template

## 🎯 Contexto do Sistema EmiteAí
Sistema de gestão de pessoas com arquitetura moderna:
- **Backend**: Spring Boot 3.2.0 + PostgreSQL + RabbitMQ
- **Frontend**: React 18 + TypeScript + Styled Components  
- **Funcionalidades**: Cadastro, auditoria, relatórios, integração ViaCEP

## 🚀 Template para Implementação de Features

### Prompt Base:
```
Implemente a funcionalidade [NOME_DA_FEATURE] no sistema EmiteAí seguindo os padrões estabelecidos do projeto.

**Especificações:**
- [Descrição detalhada da funcionalidade]
- [Regras de negócio específicas]
- [Integração com outros módulos]

**Requisitos Técnicos:**
- Seguir arquitetura MVC no backend
- Implementar validações adequadas
- Criar testes unitários e integração
- Documentar endpoints na API
- Implementar interface responsiva no frontend

**Padrões do Projeto:**
- Nomenclatura em português para domínio
- Estrutura de pastas existente
- Validações com Bean Validation
- Tratamento de erros padronizado
```

## 📋 Checklist de Implementação

### Backend
**1. Entidades e DTOs**
- [ ] Criar/atualizar entidade JPA com validações
- [ ] Implementar DTOs para request/response
- [ ] Configurar auditoria com Envers se necessário
- [ ] Mapear relacionamentos corretamente

**2. Repository Layer**
- [ ] Implementar métodos de consulta necessários
- [ ] Otimizar queries com @Query se necessário
- [ ] Adicionar métodos de busca específicos
- [ ] Implementar paginação quando aplicável

**3. Service Layer**
- [ ] Implementar lógica de negócio
- [ ] Adicionar validações customizadas
- [ ] Integrar com serviços externos se necessário
- [ ] Implementar transações adequadamente

**4. Controller Layer**
- [ ] Criar endpoints REST seguindo convenções
- [ ] Implementar validações de entrada
- [ ] Documentar com OpenAPI/Swagger
- [ ] Adicionar logs estruturados

**5. Testes Backend**
- [ ] Testes unitários de serviços
- [ ] Testes de integração de controllers
- [ ] Testes de repository
- [ ] Mocks para dependências externas

### Frontend
**1. Componentes**
- [ ] Criar componentes reutilizáveis
- [ ] Implementar tipagem TypeScript
- [ ] Adicionar styled-components
- [ ] Implementar loading states

**2. Formulários**
- [ ] Validações em tempo real
- [ ] Feedback visual de erros
- [ ] Submit com tratamento de erros
- [ ] Reset e limpeza de campos

**3. Integração API**
- [ ] Serviços de API tipados
- [ ] Tratamento de erros HTTP
- [ ] Loading e estados de carregamento
- [ ] Cache quando apropriado

**4. Testes Frontend**
- [ ] Testes de componentes com Testing Library
- [ ] Testes de integração de fluxos
- [ ] Mocks de APIs
- [ ] Testes de acessibilidade básicos

## 🛠️ Exemplos de Features

### 1. Nova Funcionalidade de Cadastro
```markdown
Implemente um sistema de cadastro de empresas no EmiteAí com:
- CNPJ como identificador único
- Integração com API de consulta CNPJ
- Relacionamento com pessoas (funcionários)
- Validações específicas para empresas
- Interface de listagem e edição
```

### 2. Sistema de Notificações
```markdown
Desenvolva um sistema de notificações para o EmiteAí incluindo:
- Notificações em tempo real com WebSocket
- Tipos: sucesso, erro, aviso, informação
- Persistência de notificações importantes
- Interface para marcar como lida
- Integração com RabbitMQ para notificações assíncronas
```

### 3. Relatórios Avançados
```markdown
Expanda o sistema de relatórios do EmiteAí com:
- Relatórios personalizáveis por período
- Gráficos e dashboards interativos
- Agendamento de relatórios
- Envio por email automatizado
- Exportação em múltiplos formatos (PDF, Excel, CSV)
```

## 🎨 Template de Resposta Esperado

```markdown
## 📦 Implementação da Feature: [NOME]

### Backend Implementation

#### 1. Entidade
```java
[Código da entidade]
```

#### 2. Repository
```java
[Código do repository]
```

#### 3. Service
```java
[Código do service]
```

#### 4. Controller
```java
[Código do controller]
```

### Frontend Implementation

#### 1. Componente Principal
```typescript
[Código do componente]
```

#### 2. Serviço API
```typescript
[Código do serviço]
```

#### 3. Estilização
```typescript
[Styled components]
```

### Tests

#### Backend Tests
```java
[Exemplos de testes]
```

#### Frontend Tests
```typescript
[Exemplos de testes]
```

### Migration Scripts
```sql
[Scripts de migração necessários]
```

### Documentation
- [Documentação da API]
- [Guia de uso da interface]

## ✅ Checklist Final
- [ ] Funcionalidade implementada completamente
- [ ] Testes passando
- [ ] Documentação atualizada
- [ ] Code review interno feito
- [ ] Deploy em ambiente de teste validado
```

## 🔄 Iteração e Refinamento

Para melhorias incrementais, use:
```
Baseado na implementação atual de [FEATURE], adicione/modifique:
- [Melhoria específica]
- [Consideração adicional]
- [Otimização necessária]

Mantenha compatibilidade com código existente e siga os mesmos padrões.
```
