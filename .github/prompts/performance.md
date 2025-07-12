# Performance Optimization Template

## ⚡ Contexto do Sistema EmiteAí
Sistema de gestão de pessoas com requisitos de performance:
- **Backend**: Spring Boot + PostgreSQL + RabbitMQ
- **Frontend**: React + TypeScript
- **Metas**: < 200ms API response, < 2s page load
- **Carga**: Suporte a milhares de usuários concorrentes

## 🚀 Template para Otimização de Performance

### Prompt Base:
```
Analise e otimize a performance do [COMPONENTE/FUNCIONALIDADE] no sistema EmiteAí.

**Problema de Performance:**
[Descrição específica do problema]

**Métricas Atuais:**
- Tempo de resposta: [X ms]
- Throughput: [X req/s]
- Uso de memória: [X MB]
- CPU utilization: [X%]

**Metas de Performance:**
- Tempo de resposta: < [X ms]
- Throughput: > [X req/s]
- Redução de memória: [X%]

**Contexto de Uso:**
- Volume de dados: [quantidade]
- Concorrência: [usuários simultâneos]
- Padrão de acesso: [read-heavy/write-heavy]

Identifique gargalos e proponha otimizações seguindo as melhores práticas.
```

## 🔍 Checklist de Performance

### Backend (Spring Boot)
- [ ] **Database Optimization**
  - [ ] Índices adequados nas consultas frequentes
  - [ ] Queries N+1 identificadas e corrigidas
  - [ ] Paginação implementada em listagens
  - [ ] Connection pool configurado adequadamente
  - [ ] Prepared statements utilizadas

- [ ] **JVM Tuning**
  - [ ] Heap size adequado para carga
  - [ ] Garbage Collector otimizado
  - [ ] Profiling de memória realizado
  - [ ] Memory leaks identificados

- [ ] **Caching Strategy**
  - [ ] Cache de aplicação (Redis/Hazelcast)
  - [ ] Cache de consultas (Hibernate Second Level)
  - [ ] Cache HTTP (ETags, Last-Modified)
  - [ ] TTL configurado adequadamente

- [ ] **Async Processing**
  - [ ] Operações pesadas assíncronas (RabbitMQ)
  - [ ] Thread pools configurados
  - [ ] Circuit breakers implementados
  - [ ] Retry logic with backoff

### Frontend (React)
- [ ] **Bundle Optimization**
  - [ ] Code splitting implementado
  - [ ] Tree shaking configurado
  - [ ] Webpack bundle analyzer usado
  - [ ] Lazy loading de componentes

- [ ] **Runtime Performance**
  - [ ] React.memo em componentes caros
  - [ ] useMemo/useCallback em computações custosas
  - [ ] Virtualization em listas grandes
  - [ ] Debounce em inputs de busca

- [ ] **Network Optimization**
  - [ ] HTTP/2 habilitado
  - [ ] Gzip/Brotli compression
  - [ ] CDN para assets estáticos
  - [ ] Service Worker para caching

## 📊 Ferramentas de Profiling

### Backend Profiling
```bash
# JProfiler - Profiling de aplicação Java
java -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849 -jar app.jar

# VisualVM - Ferramenta gratuita
jvisualvm --jdkhome $JAVA_HOME

# Spring Boot Actuator - Métricas em runtime
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Database Profiling
```sql
-- PostgreSQL - Análise de queries lentas
SELECT query, mean_time, calls, total_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Análise de índices não utilizados
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0;

-- EXPLAIN ANALYZE para queries específicas
EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM pessoa WHERE nome ILIKE '%joão%';
```

### Frontend Profiling
```javascript
// React DevTools Profiler
import { Profiler } from 'react';

function onRenderCallback(id, phase, actualDuration) {
  console.log('Component:', id, 'Phase:', phase, 'Duration:', actualDuration);
}

<Profiler id="PessoaList" onRender={onRenderCallback}>
  <PessoaList />
</Profiler>

// Web Vitals
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

getCLS(console.log);
getFID(console.log);
getFCP(console.log);
getLCP(console.log);
getTTFB(console.log);
```

## 🎯 Otimizações Específicas do EmiteAí

### Database Optimizations
```sql
-- Índices otimizados para consultas do EmiteAí
CREATE INDEX CONCURRENTLY idx_pessoa_cpf ON pessoa(cpf);
CREATE INDEX CONCURRENTLY idx_pessoa_nome_gin ON pessoa USING gin(to_tsvector('portuguese', nome));
CREATE INDEX CONCURRENTLY idx_auditoria_data ON auditoria(data_operacao) WHERE data_operacao > CURRENT_DATE - INTERVAL '30 days';

-- Particionamento da tabela de auditoria
CREATE TABLE auditoria_2024 PARTITION OF auditoria 
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

-- Vacuum e análise automática
ALTER TABLE pessoa SET (autovacuum_vacuum_scale_factor = 0.1);
ALTER TABLE auditoria SET (autovacuum_vacuum_scale_factor = 0.05);
```

### Backend Service Optimization
```java
// ❌ Antes: N+1 Query Problem
@Service
public class PessoaService {
    public List<PessoaDTO> listarPessoas() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream()
            .map(pessoa -> {
                // N+1: Uma query por pessoa para buscar endereço
                Endereco endereco = enderecoRepository.findByPessoaId(pessoa.getId());
                return mapearParaDTO(pessoa, endereco);
            })
            .collect(Collectors.toList());
    }
}

// ✅ Depois: Single Query com Join Fetch
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    @Query("SELECT p FROM Pessoa p LEFT JOIN FETCH p.endereco")
    List<Pessoa> findAllWithEndereco();
}

@Service
public class PessoaService {
    @Cacheable(value = "pessoas", unless = "#result.size() > 1000")
    public Page<PessoaDTO> listarPessoas(Pageable pageable) {
        return pessoaRepository.findAllWithEndereco(pageable)
            .map(this::mapearParaDTO);
    }
}
```

### Frontend Component Optimization
```typescript
// ❌ Antes: Re-render desnecessário
const PessoaList: React.FC = () => {
  const [pessoas, setPessoas] = useState<Pessoa[]>([]);
  const [filtro, setFiltro] = useState('');
  
  const pessoasFiltradas = pessoas.filter(p => 
    p.nome.toLowerCase().includes(filtro.toLowerCase())
  ); // Recalcula a cada render
  
  return (
    <div>
      <input onChange={(e) => setFiltro(e.target.value)} />
      {pessoasFiltradas.map(pessoa => 
        <PessoaCard key={pessoa.id} pessoa={pessoa} />
      )}
    </div>
  );
};

// ✅ Depois: Memoização adequada
const PessoaList: React.FC = () => {
  const [pessoas, setPessoas] = useState<Pessoa[]>([]);
  const [filtro, setFiltro] = useState('');
  
  // Debounce na busca
  const filtroDebounced = useDebounce(filtro, 300);
  
  // Memoização da filtragem
  const pessoasFiltradas = useMemo(() => 
    pessoas.filter(p => 
      p.nome.toLowerCase().includes(filtroDebounced.toLowerCase())
    ), [pessoas, filtroDebounced]
  );
  
  // Callback memoizado
  const handleFiltroChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    setFiltro(e.target.value);
  }, []);
  
  return (
    <div>
      <input onChange={handleFiltroChange} />
      <VirtualizedList 
        items={pessoasFiltradas}
        renderItem={({ index, style }) => (
          <div style={style}>
            <PessoaCard pessoa={pessoasFiltradas[index]} />
          </div>
        )}
      />
    </div>
  );
};

// Componente memoizado
const PessoaCard = React.memo<{ pessoa: Pessoa }>(({ pessoa }) => {
  return (
    <div>
      <h3>{pessoa.nome}</h3>
      <p>{pessoa.cpf}</p>
    </div>
  );
});
```

### Caching Strategy
```java
// Redis Configuration
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
            
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }
}

// Service com cache estratégico
@Service
public class ViaCepService {
    
    @Cacheable(value = "enderecos", key = "#cep", condition = "#cep.length() == 8")
    public EnderecoDTO buscarEnderecoPorCep(String cep) {
        // Cache por 1 hora - dados de CEP raramente mudam
        return webClient.get()
            .uri("/ws/{cep}/json/", cep)
            .retrieve()
            .bodyToMono(EnderecoDTO.class)
            .block();
    }
    
    @CacheEvict(value = "enderecos", key = "#cep")
    public void invalidarCacheEndereco(String cep) {
        // Invalidação manual se necessário
    }
}
```

### Async Processing
```java
// Configuração de Thread Pool
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("EmiteAI-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

// Processamento assíncrono de relatórios
@Service
public class RelatorioService {
    
    @Async
    @RabbitListener(queues = "relatorio.generate")
    public void gerarRelatorioAsync(RelatorioRequest request) {
        try {
            // Processamento pesado
            byte[] pdfBytes = gerarRelatorioPDF(request);
            
            // Notificar conclusão
            rabbitTemplate.convertAndSend("relatorio.completed", 
                new RelatorioCompletedEvent(request.getId(), pdfBytes));
                
        } catch (Exception e) {
            // Dead Letter Queue para falhas
            rabbitTemplate.convertAndSend("relatorio.failed", 
                new RelatorioFailedEvent(request.getId(), e.getMessage()));
        }
    }
}
```

## 📈 Monitoramento Contínuo

### Métricas Essenciais
```yaml
# Prometheus + Grafana
spring:
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.9, 0.95, 0.99
      sla:
        http.server.requests: 50ms, 100ms, 200ms, 500ms
```

### Alertas de Performance
```yaml
# AlertManager Rules
groups:
- name: emiteai-performance
  rules:
  - alert: HighResponseTime
    expr: histogram_quantile(0.95, http_request_duration_seconds) > 0.5
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High response time detected"
      
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "High error rate detected"
```

### Load Testing
```javascript
// K6 Load Testing Script
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 },   // Ramp up
    { duration: '5m', target: 100 },   // Stay at 100 users
    { duration: '2m', target: 200 },   // Ramp up to 200
    { duration: '5m', target: 200 },   // Stay at 200 users
    { duration: '2m', target: 0 },     // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% das requests < 500ms
    http_req_failed: ['rate<0.02'],    // Taxa de erro < 2%
  },
};

export default function () {
  // Teste de criação de pessoa
  let createPayload = JSON.stringify({
    nome: 'Teste Performance',
    cpf: '12345678901',
    email: 'teste@performance.com'
  });
  
  let createResponse = http.post('http://localhost:8080/api/pessoas', 
    createPayload, {
      headers: { 'Content-Type': 'application/json' },
    }
  );
  
  check(createResponse, {
    'pessoa criada': (r) => r.status === 201,
    'tempo < 200ms': (r) => r.timings.duration < 200,
  });
  
  // Teste de listagem
  let listResponse = http.get('http://localhost:8080/api/pessoas');
  check(listResponse, {
    'listagem ok': (r) => r.status === 200,
    'tempo < 100ms': (r) => r.timings.duration < 100,
  });
  
  sleep(1);
}
```

## 🎯 Benchmarks de Referência

### Backend Targets
- **API Response Time**: P95 < 200ms, P99 < 500ms
- **Throughput**: > 1000 req/s para reads, > 100 req/s para writes
- **Database**: < 50ms para queries simples, < 200ms para relatórios
- **Memory**: Heap usage < 80%, GC pause < 100ms

### Frontend Targets
- **First Contentful Paint**: < 1.5s
- **Largest Contentful Paint**: < 2.5s
- **Cumulative Layout Shift**: < 0.1
- **First Input Delay**: < 100ms
- **Bundle Size**: < 500KB inicial, < 2MB total

### Infrastructure Targets
- **CPU Utilization**: < 70% em load normal
- **Memory Usage**: < 80% disponível
- **Disk I/O**: < 80% capacity
- **Network Latency**: < 50ms entre serviços
