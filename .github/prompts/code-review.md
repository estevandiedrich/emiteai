# Code Review Template

## 📋 Contexto do Projeto
O EmiteAí é um sistema de gestão de pessoas com Spring Boot (backend) e React (frontend), incluindo:
- Cadastro de pessoas com validação de CPF
- Integração com ViaCEP para endereços
- Sistema de auditoria completo
- Geração de relatórios PDF/CSV
- Processamento assíncrono com RabbitMQ

## 🔍 Instruções para Code Review

### Backend (Spring Boot)
Analise o código considerando:

**Estrutura e Arquitetura:**
- [ ] Seguimento dos padrões MVC (Controller → Service → Repository)
- [ ] Implementação correta de DTOs para transferência de dados
- [ ] Uso adequado de anotações Spring (@Service, @Repository, @Controller)
- [ ] Separação de responsabilidades entre camadas

**Qualidade do Código:**
- [ ] Validações adequadas com Bean Validation (@Valid, @NotNull, @CPF)
- [ ] Tratamento de exceções com @ControllerAdvice
- [ ] Logs estruturados e informativos
- [ ] Nomenclatura clara de métodos e variáveis

**Segurança e Performance:**
- [ ] Sanitização de inputs
- [ ] Queries otimizadas no Repository
- [ ] Uso correto de transações (@Transactional)
- [ ] Headers de segurança configurados

**Testes:**
- [ ] Cobertura de testes unitários > 80%
- [ ] Testes de integração para endpoints críticos
- [ ] Mocks adequados para dependências externas
- [ ] Testes de validação de dados

### Frontend (React/TypeScript)
Analise o código considerando:

**Estrutura e Componentes:**
- [ ] Componentes reutilizáveis e bem organizados
- [ ] Hooks customizados para lógica compartilhada
- [ ] Tipagem TypeScript adequada
- [ ] Gerenciamento de estado eficiente

**Qualidade do Código:**
- [ ] Validação de formulários com feedback ao usuário
- [ ] Tratamento de erros da API
- [ ] Loading states e UX adequada
- [ ] Acessibilidade básica (alt texts, labels)

**Performance:**
- [ ] Lazy loading quando apropriado
- [ ] Memorização de componentes pesados
- [ ] Otimização de re-renders
- [ ] Bundle size controlado

**Testes:**
- [ ] Testes unitários de componentes
- [ ] Testes de integração de fluxos principais
- [ ] Coverage > 80%
- [ ] Testes de acessibilidade básicos

## 🎯 Checklist Específico EmiteAí

### Funcionalidades Críticas:
- [ ] Validação de CPF funcionando corretamente
- [ ] Integração ViaCEP sem falhas
- [ ] Sistema de auditoria capturando todas as operações
- [ ] Geração de relatórios funcionando
- [ ] Processamento assíncrono com RabbitMQ

### Padrões do Projeto:
- [ ] Nomenclatura em português para domínio de negócio
- [ ] Estrutura de pastas consistente
- [ ] Documentação atualizada
- [ ] Docker e docker-compose funcionais

## 📝 Template de Resposta

```markdown
## Resumo da Análise
[Descrição geral do que foi analisado]

## ✅ Pontos Positivos
- [Liste os aspectos bem implementados]

## ⚠️ Pontos de Atenção
- [Liste melhorias necessárias]

## 🔧 Sugestões de Melhoria
1. [Sugestão específica com exemplo de código se necessário]
2. [Outra sugestão]

## 🚨 Problemas Críticos
- [Liste apenas problemas que impedem deploy ou comprometem segurança]

## ✨ Recomendações Extras
- [Sugestões para melhorar qualidade/performance]
```

## 🎨 Exemplo de Análise

**Prompt Exemplo:**
"Analise o seguinte código do controller de pessoas, verificando se está seguindo os padrões do projeto EmiteAí e identificando possíveis melhorias na validação e tratamento de erros."

[Código a ser analisado]
