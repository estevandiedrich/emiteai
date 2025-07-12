# Testing Template

## 🧪 Contexto de Testes - EmiteAí
Sistema de gestão de pessoas com cobertura de testes robusta:
- **Backend**: JUnit 5 + Mockito + Spring Test + TestContainers
- **Frontend**: Jest + React Testing Library + MSW (Mock Service Worker)
- **Cobertura Meta**: >85% para código crítico

## 🎯 Template para Implementação de Testes

### Prompt Base:
```
Crie uma suíte de testes completa para [COMPONENTE/FUNCIONALIDADE] no sistema EmiteAí.

**Escopo dos Testes:**
- [Funcionalidade específica a testar]
- [Cenários de sucesso e falha]
- [Integração com outros componentes]

**Tipos de Teste Necessários:**
- Testes unitários
- Testes de integração
- Testes de contrato/API
- Testes de interface (se aplicável)

**Cenários Críticos:**
- [Lista de cenários que devem ser cobertos]

Siga os padrões de teste estabelecidos no projeto EmiteAí.
```

## 📋 Estratégia de Testes por Camada

### 🔧 Backend Testing

#### 1. **Repository Tests**
```java
@DataJpaTest
@ActiveProfiles("test")
class PessoaRepositoryTest {
    
    @Test
    void deveBuscarPorCpfQuandoExistir() {
        // Given
        Pessoa pessoa = criarPessoaValida();
        entityManager.persistAndFlush(pessoa);
        
        // When
        Optional<Pessoa> resultado = repository.findByCpf(pessoa.getCpf());
        
        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo(pessoa.getNome());
    }
}
```

**Checklist Repository:**
- [ ] Busca por ID existente/inexistente
- [ ] Métodos de query customizados
- [ ] Validações de constraints
- [ ] Paginação e ordenação
- [ ] Relacionamentos JPA

#### 2. **Service Tests**
```java
@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {
    
    @Mock
    private PessoaRepository repository;
    
    @Mock
    private ViaCepService viaCepService;
    
    @InjectMocks
    private PessoaService service;
    
    @Test
    void deveCriarPessoaComEnderecoViaViaCep() {
        // Given
        PessoaDTO dto = criarPessoaDTOValida();
        EnderecoDTO enderecoDto = criarEnderecoDTO();
        
        when(viaCepService.buscarEnderecoPorCep(dto.getCep()))
            .thenReturn(enderecoDto);
        when(repository.save(any(Pessoa.class)))
            .thenReturn(criarPessoaComId());
        
        // When
        PessoaDTO resultado = service.criarPessoa(dto);
        
        // Then
        assertThat(resultado.getId()).isNotNull();
        verify(viaCepService).buscarEnderecoPorCep(dto.getCep());
        verify(repository).save(any(Pessoa.class));
    }
}
```

**Checklist Service:**
- [ ] Lógica de negócio principal
- [ ] Validações customizadas
- [ ] Integração com serviços externos (mock)
- [ ] Tratamento de exceções
- [ ] Transações e rollback

#### 3. **Controller Tests**
```java
@WebMvcTest(PessoaController.class)
class PessoaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PessoaService service;
    
    @Test
    void deveRetornar201AoCriarPessoaValida() throws Exception {
        // Given
        PessoaDTO dto = criarPessoaDTOValida();
        when(service.criarPessoa(any())).thenReturn(dto);
        
        // When & Then
        mockMvc.perform(post("/api/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(dto.getNome()));
    }
}
```

**Checklist Controller:**
- [ ] Endpoints principais (CRUD)
- [ ] Validação de entrada (@Valid)
- [ ] Códigos de status HTTP corretos
- [ ] Serialização JSON
- [ ] Headers de resposta

#### 4. **Integration Tests**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class PessoaIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @Order(1)
    void deveRealizarFluxoCompletoDeGestãoPessoa() {
        // Criar pessoa
        PessoaDTO novaPessoa = criarPessoaDTOValida();
        ResponseEntity<PessoaDTO> response = restTemplate.postForEntity(
            "/api/pessoas", novaPessoa, PessoaDTO.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Buscar pessoa criada
        Long id = response.getBody().getId();
        ResponseEntity<PessoaDTO> getResponse = restTemplate.getForEntity(
            "/api/pessoas/" + id, PessoaDTO.class);
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### 🎨 Frontend Testing

#### 1. **Component Tests**
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CadastroPessoa } from './CadastroPessoa';

describe('CadastroPessoa', () => {
  test('deve exibir erro quando CPF inválido', async () => {
    render(<CadastroPessoa />);
    
    const cpfInput = screen.getByLabelText('CPF');
    fireEvent.change(cpfInput, { target: { value: '123.456.789-00' } });
    fireEvent.blur(cpfInput);
    
    await waitFor(() => {
      expect(screen.getByText('CPF inválido')).toBeInTheDocument();
    });
  });
  
  test('deve preencher endereço automaticamente via CEP', async () => {
    render(<CadastroPessoa />);
    
    const cepInput = screen.getByLabelText('CEP');
    fireEvent.change(cepInput, { target: { value: '01234-567' } });
    fireEvent.blur(cepInput);
    
    await waitFor(() => {
      expect(screen.getByDisplayValue('São Paulo')).toBeInTheDocument();
    });
  });
});
```

**Checklist Component:**
- [ ] Renderização inicial
- [ ] Interações do usuário
- [ ] Validações de formulário
- [ ] Estados de loading
- [ ] Tratamento de erros
- [ ] Acessibilidade básica

#### 2. **Integration Tests**
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { App } from './App';

describe('App Integration', () => {
  test('fluxo completo de cadastro de pessoa', async () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Navegar para cadastro
    fireEvent.click(screen.getByText('Cadastrar Pessoa'));
    
    // Preencher formulário
    fireEvent.change(screen.getByLabelText('Nome'), {
      target: { value: 'João Silva' }
    });
    
    // Submeter formulário
    fireEvent.click(screen.getByText('Salvar'));
    
    // Verificar sucesso
    await waitFor(() => {
      expect(screen.getByText('Pessoa cadastrada com sucesso')).toBeInTheDocument();
    });
  });
});
```

## 🧪 Tipos de Teste Específicos

### 1. **Testes de Validação**
```java
@ParameterizedTest
@ValueSource(strings = {"000.000.000-00", "111.111.111-11", "123.456.789-10"})
void deveRejeitarCpfInvalido(String cpfInvalido) {
    // Given
    PessoaDTO dto = criarPessoaDTO();
    dto.setCpf(cpfInvalido);
    
    // When & Then
    assertThrows(ValidationException.class, () -> {
        service.criarPessoa(dto);
    });
}
```

### 2. **Testes de Integração com APIs Externas**
```java
@Test
void deveConsultarViaCepCorretamente() {
    // Given
    String cep = "01310-100";
    
    // When
    EnderecoDTO resultado = viaCepService.buscarEnderecoPorCep(cep);
    
    // Then
    assertThat(resultado.getMunicipio()).isEqualTo("São Paulo");
    assertThat(resultado.getEstado()).isEqualTo("SP");
}
```

### 3. **Testes de Performance**
```java
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
void deveBuscarPessoasRapidamente() {
    // Given
    criarMultiplasPessoas(1000);
    
    // When
    List<PessoaDTO> resultado = service.buscarTodas();
    
    // Then
    assertThat(resultado).hasSize(1000);
}
```

### 4. **Testes de Segurança**
```java
@Test
void naoDevePermitirSqlInjection() {
    // Given
    String nomeComSqlInjection = "'; DROP TABLE pessoa; --";
    
    // When & Then
    assertThrows(Exception.class, () -> {
        repository.findByNomeContaining(nomeComSqlInjection);
    });
}
```

## 📊 Métricas e Cobertura

### Configuração JaCoCo (Backend)
```gradle
jacocoTestReport {
    reports {
        xml.enabled true
        html.enabled true
    }
    finalizedBy jacocoTestCoverageVerification
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.85
            }
        }
    }
}
```

### Configuração Jest (Frontend)
```json
{
  "collectCoverageFrom": [
    "src/**/*.{ts,tsx}",
    "!src/**/*.d.ts",
    "!src/index.tsx",
    "!src/serviceWorker.ts"
  ],
  "coverageThreshold": {
    "global": {
      "branches": 80,
      "functions": 80,
      "lines": 80,
      "statements": 80
    }
  }
}
```

## 🎯 Cenários de Teste Críticos EmiteAí

### 1. **Fluxo de Cadastro Completo**
- [ ] Validação de dados obrigatórios
- [ ] Consulta automática de CEP
- [ ] Validação de CPF único
- [ ] Persistência correta
- [ ] Auditoria registrada

### 2. **Sistema de Relatórios**
- [ ] Geração assíncrona com RabbitMQ
- [ ] Criação de arquivo CSV válido
- [ ] Download sem corrupção
- [ ] Limpeza de arquivos temporários

### 3. **Sistema de Auditoria**
- [ ] Captura de todas as operações CRUD
- [ ] Registro de dados de contexto
- [ ] Performance adequada
- [ ] Consulta de logs históricos

## 🚀 Template de Resposta

```markdown
## 🧪 Suíte de Testes: [COMPONENTE]

### 📋 Cobertura Implementada
- **Testes Unitários**: X testes
- **Testes de Integração**: Y testes  
- **Cobertura de Código**: Z%

### Backend Tests

#### Repository Tests
```java
[Código dos testes de repository]
```

#### Service Tests
```java
[Código dos testes de service]
```

#### Controller Tests
```java
[Código dos testes de controller]
```

### Frontend Tests

#### Component Tests
```typescript
[Código dos testes de componente]
```

#### Integration Tests
```typescript
[Código dos testes de integração]
```

### Mock Data & Utilities
```java
// Test utilities
[Código de utilitários de teste]
```

### Execution Results
```
✅ Todos os testes passando
📊 Cobertura: 89% (meta: 85%)
⏱️ Tempo de execução: 2.3s
```

## 🔄 Continuous Testing
- [ ] Testes executam no CI/CD
- [ ] Relatórios de cobertura gerados
- [ ] Falhas bloqueiam deploy
- [ ] Testes de regressão incluídos
```
