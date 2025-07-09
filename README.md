
# Emiteaí - Cadastro de Pessoa Física

## Descrição

Sistema completo para cadastro e listagem de pessoas físicas, com integração ViaCEP e geração assíncrona de relatórios CSV.

## Tecnologias

### Frontend
- React
- Material UI
- Styled-Components
- Axios
- TypeScript

### Backend
- Java 17+
- Spring Boot
- Spring Data JPA
- Flyway
- RabbitMQ
- PostgreSQL

### DevOps
- Docker
- Docker Compose
- DockerHub (build & deploy)

## Como Executar Localmente

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/emiteai
cd emiteai
```

### 2. Executar com Docker Compose

```bash
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080/swagger-ui.html
- RabbitMQ: http://localhost:15672 (login: guest/guest)

### 3. Build Manual (sem Docker Compose)

#### Backend
```bash
cd backend
./mvnw clean package -DskipTests
```

#### Frontend
```bash
cd frontend
npm install && npm run build
```

### 4. Publicar Imagens no DockerHub

No diretório de cada projeto (backend e frontend):

```bash
docker build -t seu-usuario/emiteai-backend .
docker push seu-usuario/emiteai-backend

docker build -t seu-usuario/emiteai-frontend .
docker push seu-usuario/emiteai-frontend
```

## Como Executar os Testes

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## Funcionalidades

- Cadastro de pessoa com auto-preenchimento ViaCEP
- Listagem e exclusão de pessoas
- Geração assíncrona de CSV
- Download manual do CSV
- Auditoria de todas as requisições
