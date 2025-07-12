# EmiteAí - Sistema de Gestão de Pessoas

## Sobre o Projeto

O EmiteAí é um sistema completo de gestão de pessoas, desenvolvido com Spring Boot (backend) e React (frontend). O sistema oferece funcionalidades de cadastro, consulta, auditoria e geração de relatórios de pessoas, com integração automática de endereços através da API ViaCEP.

## Recursos Principais

- **Cadastro de Pessoas**: Formulário completo com validação de dados
- **Consulta de Endereços**: Integração automática com ViaCEP para preenchimento de endereços
- **Auditoria Completa**: Rastreamento de todas as operações (inclusão, alteração, exclusão)
- **Relatórios**: Geração de relatórios em PDF e CSV
- **Listagem Avançada**: Filtros, ordenação e paginação
- **Validações**: CPF, campos obrigatórios e formatos de dados

## Tecnologias

### Backend
- Spring Boot 3.2.0, Spring Data JPA, Spring Web
- H2 Database, Flyway, RabbitMQ
- OpenPDF, OpenCSV para relatórios

### Frontend
- React 18, TypeScript, Axios
- React Router, Styled Components
- React Hook Form

### Infraestrutura
- Docker & Docker Compose
- RabbitMQ, Nginx

## Execução

### Com Docker (Recomendado)

```bash
git clone <repository-url>
cd emiteai_project_complete
docker-compose up --build
```

**Acesso:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- RabbitMQ Management: http://localhost:15672 (admin/admin)

### Execução Manual

**Backend:**
```bash
cd backend
./gradlew bootRun
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

## Testes

```bash
# Backend
cd backend && ./gradlew test

# Frontend
cd frontend && npm test
```

## API Endpoints

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/api/pessoas` | GET | Listar pessoas |
| `/api/pessoas` | POST | Criar pessoa |
| `/api/pessoas/{id}` | PUT | Atualizar pessoa |
| `/api/pessoas/{id}` | DELETE | Excluir pessoa |
| `/api/cep/{cep}` | GET | Consultar endereço por CEP |
| `/api/auditoria` | GET | Listar logs de auditoria |
| `/api/relatorios/pessoas/pdf` | GET | Relatório PDF |
| `/api/relatorios/pessoas/csv` | GET | Relatório CSV |

## Configuração

### Variáveis de Ambiente
- `RABBITMQ_HOST`: Host do RabbitMQ
- `RABBITMQ_PORT`: Porta do RabbitMQ
- `RABBITMQ_USERNAME`: Usuário do RabbitMQ
- `RABBITMQ_PASSWORD`: Senha do RabbitMQ

### Banco de Dados
Para produção, configure PostgreSQL ou MySQL no `application.yml`.

## Arquitetura

```
emiteai_project_complete/
├── backend/                 # API Spring Boot
│   ├── src/main/java/      # Código fonte
│   ├── src/main/resources/ # Configurações e migrações
│   └── src/test/           # Testes
├── frontend/               # Aplicação React
│   ├── src/                # Código fonte
│   ├── public/             # Arquivos públicos
│   └── build/              # Build de produção
├── docker-compose.yml      # Orquestração de containers
└── init-db.sql            # Scripts de inicialização
```

## Funcionalidades

### 1. Cadastro de Pessoas
- Formulário com validação completa
- Consulta automática de endereço via CEP
- Validação de CPF

### 2. Listagem de Pessoas
- Tabela paginada com filtros
- Ordenação por colunas
- Ações de editar e excluir

### 3. Auditoria
- Registro automático de operações
- Visualização de logs com filtros

### 4. Relatórios
- Geração de PDF e CSV
- Filtros aplicáveis

## Monitoramento

- Logs estruturados com níveis apropriados
- Actuator endpoints disponíveis
- Health checks configurados

## Segurança

- Validação de entrada em todos os endpoints
- CORS configurado
- Headers de segurança aplicados

## Licença

Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
