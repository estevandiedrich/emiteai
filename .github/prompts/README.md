# 🤖 Prompt Templates - EmiteAí

Esta pasta contém templates de prompts personalizados para auxiliar no desenvolvimento do sistema EmiteAí. Cada template é otimizado para tarefas específicas e segue os padrões estabelecidos no projeto.

## 📋 Templates Disponíveis

### 🔍 [code-review.md](./code-review.md)
**Uso**: Code reviews estruturados  
**Quando usar**: Antes de merges, revisões de qualidade de código  
**Cobre**: Padrões MVC, validações, segurança, performance, testes

### 🚀 [feature-development.md](./feature-development.md)  
**Uso**: Implementação de novas funcionalidades  
**Quando usar**: Desenvolvimento de features, módulos, integrações  
**Cobre**: Backend, frontend, testes, documentação, padrões do projeto

### 🐛 [bug-fix.md](./bug-fix.md)
**Uso**: Correção de bugs e problemas  
**Quando usar**: Análise de falhas, debugging, hotfixes  
**Cobre**: Reprodução, análise de causa raiz, correção, prevenção

### 🧪 [testing.md](./testing.md)
**Uso**: Estratégias e implementação de testes  
**Quando usar**: Criação de testes unitários, integração, E2E  
**Cobre**: JUnit, Mockito, Jest, React Testing Library, cobertura

### ♻️ [refactoring.md](./refactoring.md)
**Uso**: Refatoração e melhoria de código  
**Quando usar**: Cleanup, otimização, aplicação de padrões  
**Cobre**: SOLID, Clean Code, Extract Method/Class, performance

### 📚 [documentation.md](./documentation.md)
**Uso**: Criação e manutenção de documentação  
**Quando usar**: APIs, READMEs, ADRs, guias de deploy  
**Cobre**: OpenAPI, Markdown, diagramas, troubleshooting

### ⚡ [performance.md](./performance.md)
**Uso**: Otimização de performance  
**Quando usar**: Problemas de lentidão, escalabilidade, profiling  
**Cobre**: JVM tuning, caching, queries, bundle optimization

## 🎯 Como Usar

### 1. Selecione o Template Adequado
Escolha o template baseado na sua necessidade:
- **Desenvolvimento**: `feature-development.md`
- **Correção**: `bug-fix.md`  
- **Qualidade**: `code-review.md` ou `refactoring.md`
- **Testes**: `testing.md`
- **Docs**: `documentation.md`
- **Performance**: `performance.md`

### 2. Copie o Prompt Base
Cada template contém um "Prompt Base" que você pode copiar e personalizar:

```markdown
<!-- Exemplo do feature-development.md -->
Implemente a funcionalidade [NOME_DA_FEATURE] no sistema EmiteAí seguindo os padrões estabelecidos do projeto.

**Especificações:**
- [Sua descrição aqui]
- [Suas regras de negócio]
```

### 3. Personalize para Seu Caso
Substitua os placeholders `[TEXTO]` pelas informações específicas do seu contexto.

### 4. Use com AI Assistant
Cole o prompt personalizado no seu AI assistant favorito (GitHub Copilot, ChatGPT, Claude, etc.).

## 💡 Exemplos Práticos

### Implementar Nova Feature
```markdown
Implemente a funcionalidade Sistema de Notificações no sistema EmiteAí seguindo os padrões estabelecidos do projeto.

**Especificações:**
- Notificar usuários sobre cadastros realizados
- Suporte a email e push notifications
- Dashboard para gerenciar preferências

**Requisitos Técnicos:**
- Integração com SendGrid para emails
- WebSockets para notificações em tempo real
- RabbitMQ para processamento assíncrono
```

### Code Review Focado
```markdown
Faça um code review do controller PessoaController focando em:

**Pontos de Atenção:**
- Validação de entrada nos endpoints
- Tratamento de exceções adequado
- Documentação OpenAPI completa
- Performance das consultas

**Arquivo:** `/backend/src/main/java/com/emiteai/controller/PessoaController.java`
```

### Debug de Performance
```markdown
Analise e otimize a performance da funcionalidade Listagem de Pessoas no sistema EmiteAí.

**Problema de Performance:**
Listagem com 10.000+ registros está levando > 5 segundos

**Métricas Atuais:**
- Tempo de resposta: 5.2 segundos
- Query executadas: 1 + N queries (N+1 problem)
- Uso de memória: 500MB durante operação

**Metas de Performance:**
- Tempo de resposta: < 500ms
- Single query ou queries otimizadas
- Paginação implementada
```

## 🔧 Customização

### Adaptando para Outros Projetos
Os templates são específicos do EmiteAí mas podem ser adaptados:

1. **Substitua referências do projeto**:
   - "EmiteAí" → "SeuProjeto"
   - Stack tecnológica específica
   - Padrões e convenções próprias

2. **Ajuste os checklists**:
   - Adicione/remova itens conforme sua arquitetura
   - Inclua ferramentas específicas do seu ambiente
   - Adapte métricas de performance

3. **Personalize exemplos**:
   - Use casos de uso do seu domínio
   - Inclua padrões específicos da sua equipe

### Criando Novos Templates
Para criar templates adicionais:

1. **Estrutura base**:
   ```markdown
   # [Nome do Template]
   
   ## 🎯 Contexto do Sistema
   [Descrição do contexto]
   
   ## 📝 Template para [Ação]
   
   ### Prompt Base:
   ```
   [Prompt genérico]
   ```
   
   ## ✅ Checklist
   [Lista de verificação]
   
   ## 💡 Exemplos
   [Exemplos práticos]
   ```

2. **Nomeação**: Use formato `kebab-case.md`
3. **Documentação**: Adicione entrada neste README

## 🤝 Contribuindo

### Melhorias nos Templates
- **Feedback**: Reporte problemas ou melhorias via issues
- **Exemplos**: Compartilhe casos de uso que funcionaram bem
- **Atualizações**: Mantenha templates alinhados com evolução do projeto

### Novos Templates
Sugestões para templates adicionais:
- **Security Review**: Análise de segurança
- **Database Migration**: Scripts de migração
- **API Integration**: Integração com APIs externas
- **Deployment**: Estratégias de deploy
- **Monitoring**: Implementação de observabilidade

## 📚 Recursos Complementares

### Ferramentas Recomendadas
- **GitHub Copilot**: Integração nativa com VS Code
- **ChatGPT**: Interface web para prompts longos
- **Claude**: Bom para análise de código e documentação
- **Codeium**: Alternativa gratuita ao Copilot

### Documentação de Referência
- [Spring Boot Best Practices](https://spring.io/guides)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Clean Code Principles](https://clean-code-developer.com/)
- [API Design Guidelines](https://github.com/microsoft/api-guidelines)

---

💡 **Dica**: Combine templates quando necessário. Por exemplo, use `feature-development.md` + `testing.md` para implementar uma feature com testes completos.
