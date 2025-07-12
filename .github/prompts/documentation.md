# Documentation Template

## 📚 Contexto do Sistema EmiteAí
Sistema de gestão de pessoas com documentação técnica completa:
- **Arquitetura**: Microserviços com Spring Boot + React
- **Padrões**: OpenAPI, README estruturado, Javadoc
- **Audiência**: Desenvolvedores, DevOps, Product Owners

## 📝 Template para Documentação Técnica

### Prompt Base:
```
Crie/atualize a documentação técnica para [COMPONENTE/FEATURE] no sistema EmiteAí.

**Tipo de Documentação:**
- [ ] README de módulo/feature
- [ ] Documentação de API (OpenAPI)
- [ ] Guia de arquitetura
- [ ] Manual de instalação/deploy
- [ ] Troubleshooting guide
- [ ] ADR (Architecture Decision Record)

**Audiência Alvo:**
- [ ] Desenvolvedores iniciantes no projeto
- [ ] Desenvolvedores experientes
- [ ] DevOps/SRE
- [ ] Product Owners
- [ ] Usuários finais

**Contexto Específico:**
[Descreva o que precisa ser documentado e por quê]

Siga os padrões de documentação estabelecidos no projeto EmiteAí.
```

## 🎯 Estruturas de Documentação

### README de Feature
```markdown
# [Nome da Feature]

## 📋 Visão Geral
Breve descrição da funcionalidade e seu propósito no sistema.

## 🚀 Funcionalidades
- [ ] Feature 1: Descrição
- [ ] Feature 2: Descrição
- [ ] Feature 3: Descrição

## 🏗️ Arquitetura
### Backend
```
src/main/java/com/emiteai/
├── controller/     # Endpoints REST
├── service/        # Lógica de negócio
├── repository/     # Acesso a dados
└── dto/           # Objetos de transferência
```

### Frontend
```
src/app/
├── pages/         # Páginas principais
├── components/    # Componentes reutilizáveis
├── services/      # Comunicação com API
└── types/         # Definições TypeScript
```

## 🔧 Configuração
### Pré-requisitos
- Java 17+
- Node.js 18+
- PostgreSQL 13+
- Docker (opcional)

### Instalação
```bash
# Backend
cd backend
./mvnw clean install

# Frontend  
cd frontend
npm install
```

## 📖 Como Usar
### Exemplos de Uso
[Exemplos práticos da funcionalidade]

### API Endpoints
[Documentação dos endpoints se aplicável]

## 🧪 Testes
```bash
# Executar testes
./mvnw test                    # Backend
npm test                       # Frontend
```

## 🐛 Troubleshooting
### Problemas Comuns
1. **Erro X**: Causa e solução
2. **Erro Y**: Causa e solução

## 🤝 Contribuição
[Guidelines para contribuir com esta feature]
```

### API Documentation (OpenAPI)
```yaml
openapi: 3.0.1
info:
  title: EmiteAí API
  description: Sistema de gestão de pessoas
  version: 1.0.0
  contact:
    name: Equipe EmiteAí
    email: dev@emiteai.com

servers:
  - url: http://localhost:8080/api
    description: Desenvolvimento local
  - url: https://api.emiteai.com
    description: Produção

paths:
  /pessoas:
    post:
      summary: Cadastrar pessoa
      description: Cria uma nova pessoa no sistema
      operationId: criarPessoa
      tags:
        - Pessoas
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PessoaDTO'
            examples:
              exemplo_basico:
                summary: Pessoa básica
                value:
                  nome: "João Silva"
                  cpf: "12345678901"
                  email: "joao@example.com"
      responses:
        '201':
          description: Pessoa criada com sucesso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Pessoa'
        '400':
          description: Dados inválidos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  schemas:
    PessoaDTO:
      type: object
      required:
        - nome
        - cpf
      properties:
        nome:
          type: string
          description: Nome completo da pessoa
          example: "João Silva"
          minLength: 2
          maxLength: 100
        cpf:
          type: string
          description: CPF da pessoa (apenas números)
          example: "12345678901"
          pattern: '^[0-9]{11}$'
        email:
          type: string
          format: email
          description: Email da pessoa
          example: "joao@example.com"
```

### Architecture Decision Record (ADR)
```markdown
# ADR-001: Escolha do Banco de Dados

## Status
Aceito

## Contexto
O sistema EmiteAí precisa de um banco de dados relacional para:
- Armazenar dados de pessoas com integridade referencial
- Suportar auditoria com Envers
- Facilitar relatórios e consultas complexas

## Decisão
Utilizaremos PostgreSQL como banco de dados principal.

## Consequências

### Positivas
- ✅ Excelente suporte para JSON e tipos avançados
- ✅ ACID compliant e alta confiabilidade
- ✅ Boa integração com Spring Boot e Hibernate
- ✅ Suporte nativo para auditoria temporal

### Negativas
- ❌ Mais complexo que SQLite para desenvolvimento
- ❌ Requer mais recursos que bancos NoSQL
- ❌ Curva de aprendizado para desenvolvedores novatos

## Alternativas Consideradas
1. **MySQL**: Menos recursos avançados
2. **SQLite**: Limitado para produção
3. **MongoDB**: Não atende requisitos relacionais
```

### Deployment Guide
```markdown
# Guia de Deploy - EmiteAí

## 🎯 Ambientes

### Desenvolvimento Local
```bash
# Usando Docker Compose
docker-compose up -d

# Ou manual
cd backend && ./mvnw spring-boot:run
cd frontend && npm start
```

### Staging
```bash
# Build das imagens
docker build -t emiteai-backend:staging ./backend
docker build -t emiteai-frontend:staging ./frontend

# Deploy
kubectl apply -f k8s/staging/
```

### Produção
```bash
# CI/CD Pipeline
# 1. Tests executados automaticamente
# 2. Build das imagens
# 3. Deploy com Blue/Green strategy

# Verificação pós-deploy
curl -f https://api.emiteai.com/health
```

## 🔧 Configuração de Ambiente

### Variáveis Obrigatórias
```bash
# Backend
DATABASE_URL=postgresql://user:pass@host:5432/emiteai
RABBITMQ_URL=amqp://user:pass@host:5672/
JWT_SECRET=your-secret-key
VIACEP_API_URL=https://viacep.com.br/ws

# Frontend
REACT_APP_API_URL=https://api.emiteai.com
REACT_APP_ENVIRONMENT=production
```

## 📊 Monitoramento
- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Logs**: Centralizados no ELK Stack
- **Alertas**: Configurados no Grafana
```

## 📋 Checklist de Documentação

### Para Cada Feature Nova:
- [ ] **README específico** da feature criado
- [ ] **Endpoints documentados** no OpenAPI
- [ ] **Exemplos de uso** incluídos
- [ ] **Testes documentados** com exemplos
- [ ] **Troubleshooting** atualizado
- [ ] **Changelog** atualizado

### Para Mudanças de Arquitetura:
- [ ] **ADR criado** explicando a decisão
- [ ] **Diagramas atualizados** se necessário
- [ ] **Guias de migração** se breaking changes
- [ ] **Timeline de deprecação** definida
- [ ] **Comunicação** para equipe feita

### Para Releases:
- [ ] **Release notes** detalhadas
- [ ] **Breaking changes** destacadas
- [ ] **Guia de upgrade** disponível
- [ ] **Documentação de API** versionada
- [ ] **Exemplos atualizados** para nova versão

## 🎨 Padrões de Escrita

### Tom e Estilo:
- **Linguagem clara** e objetiva
- **Exemplos práticos** sempre que possível
- **Screenshots** para interfaces complexas
- **Códigos comentados** em exemplos
- **Links internos** para referências cruzadas

### Estrutura:
1. **Contexto** - Por que existe
2. **Como usar** - Exemplos práticos
3. **Configuração** - Setup necessário
4. **Troubleshooting** - Problemas comuns
5. **Referências** - Links úteis

### Manutenção:
- **Review trimestral** de toda documentação
- **Atualização automática** de exemplos em CI
- **Feedback dos usuários** coletado e processado
- **Métricas de uso** da documentação monitoradas

## 🔗 Ferramentas Recomendadas

### Documentação de API:
- **Swagger UI**: Interface interativa
- **Postman Collections**: Exemplos testáveis
- **Insomnia**: Alternativa ao Postman

### Diagramas:
- **Mermaid**: Diagramas como código
- **Draw.io**: Editor visual
- **PlantUML**: UML como código

### Versionamento:
- **GitBook**: Documentação versionada
- **Confluence**: Wiki corporativo
- **Notion**: Documentação colaborativa

## 📚 Templates Rápidos

### Bug Report
```markdown
## 🐛 Descrição do Bug
[Descrição clara e concisa do bug]

## 🔄 Passos para Reproduzir
1. Vá para '...'
2. Clique em '...'
3. Role para baixo até '...'
4. Veja o erro

## ✅ Comportamento Esperado
[Descreva o que deveria acontecer]

## 📱 Screenshots
[Se aplicável, adicione screenshots]

## 🖥️ Ambiente
- OS: [e.g. Windows 10]
- Browser: [e.g. Chrome 91]
- Versão: [e.g. 1.2.3]
```

### Feature Request
```markdown
## 🚀 Resumo da Feature
[Descrição clara da funcionalidade solicitada]

## 💡 Motivação
[Por que esta feature é importante?]

## 📋 Critérios de Aceitação
- [ ] Critério 1
- [ ] Critério 2
- [ ] Critério 3

## 🎨 Mockups/Wireframes
[Se disponível, anexe mockups]

## 🔗 Recursos Adicionais
[Links, referências, exemplos de outros sistemas]
```
