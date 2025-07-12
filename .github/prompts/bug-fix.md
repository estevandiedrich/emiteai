# Bug Fix Template

## 🐛 Contexto do Sistema EmiteAí
Sistema de gestão de pessoas com stack:
- **Backend**: Spring Boot + PostgreSQL + RabbitMQ + Envers
- **Frontend**: React + TypeScript + Styled Components
- **Infraestrutura**: Docker + Nginx

## 🔧 Template para Correção de Bugs

### Prompt Base:
```
Analise e corrija o seguinte bug no sistema EmiteAí:

**Descrição do Bug:**
[Descrição detalhada do comportamento incorreto]

**Passos para Reproduzir:**
1. [Passo 1]
2. [Passo 2]
3. [Resultado esperado vs obtido]

**Ambiente:**
- Browser: [se aplicável]
- Dados de teste: [exemplos específicos]
- Logs de erro: [se disponíveis]

**Contexto Adicional:**
[Informações relevantes sobre quando o bug apareceu]
```

## 🔍 Processo de Análise

### 1. Investigação Inicial
**Backend Issues:**
- [ ] Verificar logs da aplicação
- [ ] Analisar queries executadas
- [ ] Checar validações e constraints
- [ ] Revisar tratamento de exceções
- [ ] Verificar transações e rollbacks

**Frontend Issues:**
- [ ] Verificar console do browser
- [ ] Analisar network requests
- [ ] Checar estado dos componentes
- [ ] Revisar event handlers
- [ ] Verificar validações de formulário

**Infraestrutura Issues:**
- [ ] Verificar logs do Docker
- [ ] Checar conectividade com banco
- [ ] Analisar configurações do RabbitMQ
- [ ] Revisar configurações do Nginx

### 2. Tipos Comuns de Bugs EmiteAí

#### 🔴 CPF e Validações
```markdown
**Bug**: Validação de CPF aceitando formatos inválidos
**Locais**: Validation annotations, frontend masks, service validations
**Checklist**:
- [ ] Regex patterns corretos
- [ ] Validação de dígitos verificadores
- [ ] Tratamento de caracteres especiais
- [ ] Feedback visual adequado
```

#### 🔴 Integração ViaCEP
```markdown
**Bug**: Falha na consulta de endereços
**Locais**: ViaCepService, error handling, timeout configs
**Checklist**:
- [ ] Conectividade com API externa
- [ ] Tratamento de respostas vazias/inválidas
- [ ] Timeout e retry configs
- [ ] Fallback para CEPs não encontrados
```

#### 🔴 Sistema de Auditoria
```markdown
**Bug**: Logs de auditoria incompletos ou incorretos
**Locais**: Envers config, CustomRevisionListener, audit interceptors
**Checklist**:
- [ ] Configuração de entidades auditadas
- [ ] Captura de dados de contexto (user, IP)
- [ ] Timestamps corretos
- [ ] Performance da auditoria
```

#### 🔴 Relatórios e CSV
```markdown
**Bug**: Falha na geração ou download de relatórios
**Locais**: RabbitMQ processing, file generation, download endpoints
**Checklist**:
- [ ] Processamento assíncrono funcionando
- [ ] Geração de arquivos sem corrupção
- [ ] Limpeza de arquivos temporários
- [ ] Headers HTTP corretos para download
```

#### 🔴 Interface e UX
```markdown
**Bug**: Problemas de responsividade ou interação
**Locais**: Styled components, event handlers, state management
**Checklist**:
- [ ] Layout em diferentes resoluções
- [ ] Loading states adequados
- [ ] Feedback visual de ações
- [ ] Navegação e routing
```

## 🛠️ Template de Correção

### Estrutura de Resposta:
```markdown
## 🔍 Análise do Bug

### Root Cause
[Descrição clara da causa raiz do problema]

### Impacto
- **Severidade**: [Crítico/Alto/Médio/Baixo]
- **Usuários Afetados**: [Descrição]
- **Funcionalidades Comprometidas**: [Lista]

## 🔧 Solução Implementada

### Código Corrigido

#### Backend Changes
```java
// Antes (problemático)
[código com bug]

// Depois (corrigido)
[código corrigido]
```

#### Frontend Changes
```typescript
// Antes (problemático)
[código com bug]

// Depois (corrigido)
[código corrigido]
```

### Configurações Ajustadas
```yaml
# application.yml changes
[mudanças necessárias]
```

## 🧪 Testes de Validação

### Testes Unitários Adicionados
```java
@Test
void testBugFix() {
    // Teste que reproduz o cenário do bug
    // e valida a correção
}
```

### Testes de Integração
```java
@Test
void testIntegrationScenario() {
    // Teste end-to-end do fluxo corrigido
}
```

### Testes Manuais
- [ ] [Cenário 1 de teste manual]
- [ ] [Cenário 2 de teste manual]

## 🔄 Prevenção

### Melhorias Implementadas
- [Lista de melhorias para prevenir bugs similares]

### Monitoramento Adicionado
- [Logs/métricas adicionados para detectar problemas similares]

## 📋 Checklist de Deploy
- [ ] Testes automatizados passando
- [ ] Validação manual em ambiente de teste
- [ ] Backup do banco antes do deploy
- [ ] Rollback plan definido
- [ ] Monitoramento post-deploy configurado
```

## 🚨 Bugs Críticos - Ação Imediata

Para bugs críticos que afetam produção:

```markdown
HOTFIX NECESSÁRIO - Sistema EmiteAí

**Bug Crítico**: [Descrição concisa]
**Impacto**: [Usuários/funcionalidades afetadas]
**Urgência**: [Imediata/Alta]

Implemente correção mínima e segura focando em:
1. Restaurar funcionalidade principal
2. Manter integridade dos dados
3. Não quebrar outras funcionalidades
4. Permitir rollback fácil se necessário

Código atual problemático:
[código com problema]

Solução temporária/definitiva:
[correção proposta]
```

## 📊 Exemplos de Bugs Reais

### Exemplo 1: Validação CPF
```markdown
**Bug**: Sistema aceita CPF "000.000.000-00" como válido
**Causa**: Validação checando apenas formato, não dígitos verificadores
**Solução**: Implementar algoritmo completo de validação CPF
**Arquivos**: CPFValidator.java, CadastroPessoa.tsx
```

### Exemplo 2: Memory Leak em Relatórios
```markdown
**Bug**: Memória do servidor aumenta após cada geração de relatório
**Causa**: Arquivos temporários não sendo limpos
**Solução**: Implementar cleanup automático com @Scheduled
**Arquivos**: CsvService.java, file cleanup configuration
```

### Exemplo 3: Responsive Layout
```markdown
**Bug**: Tabela de pessoas quebra em mobile
**Causa**: Styled components sem breakpoints adequados
**Solução**: Implementar design responsivo com grid/flexbox
**Arquivos**: ListagemPessoas.tsx, styled components
```
