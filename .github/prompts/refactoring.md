# Refactoring Template

## 🔧 Contexto do Sistema EmiteAí
Sistema de gestão de pessoas com arquitetura limpa e padrões bem definidos:
- **Backend**: Spring Boot 3.2.0 + PostgreSQL + RabbitMQ + Envers
- **Frontend**: React 18 + TypeScript + Styled Components
- **Princípios**: Clean Code, SOLID, padrões de arquitetura MVC

## ♻️ Template para Refatoração

### Prompt Base:
```
Refatore o seguinte código no sistema EmiteAí mantendo a funcionalidade existente:

**Código Atual:**
[Cole o código que precisa ser refatorado]

**Problemas Identificados:**
- [ ] Duplicação de código
- [ ] Métodos muito longos
- [ ] Responsabilidades múltiplas
- [ ] Nomes pouco descritivos
- [ ] Dependências desnecessárias
- [ ] Performance inadequada
- [ ] Outros: [especificar]

**Objetivos da Refatoração:**
- Melhorar legibilidade e manutenibilidade
- Reduzir complexidade ciclomática
- Aplicar princípios SOLID
- Otimizar performance se necessário
- Manter compatibilidade com testes existentes

**Restrições:**
- Manter API pública inalterada
- Preservar comportamento atual
- Não quebrar testes existentes
```

## 🎯 Checklist de Refatoração

### Backend (Spring Boot)
- [ ] **Single Responsibility**: Cada classe tem uma única responsabilidade
- [ ] **Open/Closed**: Aberto para extensão, fechado para modificação
- [ ] **Dependency Inversion**: Depender de abstrações, não de implementações
- [ ] **Clean Methods**: Métodos com no máximo 20 linhas
- [ ] **Meaningful Names**: Nomes expressivos para classes, métodos e variáveis
- [ ] **Error Handling**: Tratamento adequado de exceções
- [ ] **Constants**: Valores mágicos extraídos para constantes
- [ ] **Resource Management**: Uso adequado de try-with-resources

### Frontend (React/TypeScript)
- [ ] **Component Separation**: Componentes com responsabilidade única
- [ ] **Custom Hooks**: Lógica reutilizável extraída para hooks
- [ ] **Type Safety**: Tipagem adequada em TypeScript
- [ ] **Performance**: Uso adequado de useMemo, useCallback
- [ ] **Accessibility**: Componentes acessíveis (a11y)
- [ ] **Styled Components**: CSS-in-JS organizado e reutilizável
- [ ] **State Management**: Estado local vs global bem definido

## 🔍 Padrões de Refatoração Comuns

### Extract Method
**Quando usar**: Métodos muito longos ou com lógica duplicada
```java
// Antes
public void processarPessoa(Pessoa pessoa) {
    // 50 linhas de código...
}

// Depois
public void processarPessoa(Pessoa pessoa) {
    validarPessoa(pessoa);
    enriquecerComEndereco(pessoa);
    salvarPessoa(pessoa);
    notificarProcessamento(pessoa);
}
```

### Extract Class
**Quando usar**: Classes com múltiplas responsabilidades
```java
// Antes: PessoaService faz validação + integração + persistência

// Depois: 
// - PessoaValidator (validação)
// - EnderecoService (integração ViaCEP)
// - PessoaRepository (persistência)
```

### Replace Magic Numbers
**Quando usar**: Números sem contexto no código
```java
// Antes
if (pessoa.getIdade() > 18) { ... }

// Depois
private static final int IDADE_MAIORIDADE = 18;
if (pessoa.getIdade() > IDADE_MAIORIDADE) { ... }
```

### Introduce Parameter Object
**Quando usar**: Muitos parâmetros em métodos
```java
// Antes
public Relatorio gerarRelatorio(String tipo, Date inicio, Date fim, 
                               String formato, boolean incluirTotais) { ... }

// Depois
public Relatorio gerarRelatorio(RelatorioRequest request) { ... }
```

## 🧪 Estratégia de Testes para Refatoração

### Pré-Refatoração:
1. **Executar todos os testes** para garantir estado verde
2. **Adicionar testes de caracterização** se cobertura for baixa
3. **Documentar comportamento atual** em casos complexos

### Durante a Refatoração:
1. **Baby steps**: Mudanças pequenas e incrementais
2. **Red-Green-Refactor**: Sempre manter testes passando
3. **Commit frequente**: Cada melhoria isolada

### Pós-Refatoração:
1. **Executar suite completa** de testes
2. **Verificar cobertura** mantida ou melhorada
3. **Review de performance** se aplicável
4. **Documentar mudanças** significativas

## 🚀 Exemplos de Refatoração no EmiteAí

### Backend - Service Layer
```java
// ❌ Antes: Responsabilidades múltiplas
@Service
public class PessoaService {
    public void salvarPessoa(PessoaDTO dto) {
        // Validação manual
        if (dto.getCpf() == null || dto.getCpf().isEmpty()) {
            throw new IllegalArgumentException("CPF obrigatório");
        }
        
        // Integração ViaCEP inline
        RestTemplate rest = new RestTemplate();
        String url = "https://viacep.com.br/ws/" + dto.getCep() + "/json/";
        // ... código de integração ...
        
        // Conversão manual
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        // ... mapeamento manual ...
        
        repository.save(pessoa);
    }
}

// ✅ Depois: Responsabilidades separadas
@Service
public class PessoaService {
    private final PessoaValidator validator;
    private final EnderecoService enderecoService;
    private final PessoaMapper mapper;
    private final PessoaRepository repository;
    
    public void salvarPessoa(PessoaDTO dto) {
        validator.validar(dto);
        enderecoService.enriquecerComEndereco(dto);
        Pessoa pessoa = mapper.toEntity(dto);
        repository.save(pessoa);
    }
}
```

### Frontend - Component Refactoring
```typescript
// ❌ Antes: Componente com múltiplas responsabilidades
const CadastroPessoa = () => {
    const [nome, setNome] = useState('');
    const [cpf, setCpf] = useState('');
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        
        // Validação inline
        const newErrors = {};
        if (!nome) newErrors.nome = 'Nome obrigatório';
        if (!cpf) newErrors.cpf = 'CPF obrigatório';
        
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            setLoading(false);
            return;
        }
        
        // API call inline
        try {
            const response = await fetch('/api/pessoas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome, cpf })
            });
            // ... resto da lógica ...
        } catch (error) {
            // ... tratamento ...
        } finally {
            setLoading(false);
        }
    };
    
    return (
        <form onSubmit={handleSubmit}>
            {/* JSX muito longo... */}
        </form>
    );
};

// ✅ Depois: Responsabilidades separadas
const CadastroPessoa = () => {
    const { formData, errors, updateField } = usePessoaForm();
    const { submitPessoa, loading } = usePessoaAPI();
    
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        await submitPessoa(formData);
    };
    
    return (
        <PessoaForm 
            data={formData}
            errors={errors}
            loading={loading}
            onChange={updateField}
            onSubmit={handleSubmit}
        />
    );
};
```

## 📚 Recursos e Referências

### Livros Recomendados:
- **Clean Code** - Robert Martin
- **Refactoring** - Martin Fowler  
- **Effective Java** - Joshua Bloch

### Ferramentas de Análise:
- **SonarQube**: Análise estática de código
- **ESLint**: Padrões JavaScript/TypeScript
- **SpotBugs**: Detecção de bugs Java

### Métricas de Qualidade:
- **Complexidade Ciclomática**: < 10 por método
- **Cobertura de Testes**: > 85% para código crítico
- **Duplicação**: < 3% do código total
- **Débito Técnico**: Monitoramento contínuo
