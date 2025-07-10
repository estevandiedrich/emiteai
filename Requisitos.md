
# Avaliação Técnica - Emiteaí

Olá,

Você está recebendo a avaliação técnica do Emiteaí.

Nossa avaliação visa testar os conhecimentos técnicos, conceituais e boas práticas de programação e tecnologia.  
Essa avaliação deverá ser entregue até a data que consta na descrição do e-mail.

## Requisitos de Negócio

- Será necessário desenvolver uma aplicação que realizará cadastros de pessoa física.
- Considere as seguintes informações no cadastro de pessoa:
  - Nome (character)
  - Telefone (character)
  - CPF (character) **(validar duplicidade)**
  - Endereço:
    - Número (character)
    - Complemento (character)
    - CEP (deve ser auto-preenchido quando for digitado utilizando a API aberta da [ViaCEP](https://viacep.com.br/)) (character)
    - Bairro (character) *(preenchido pela API ViaCEP)*
    - Nome do Município (character) *(preenchido pela API ViaCEP)*
    - Nome do Estado (character) *(preenchido pela API ViaCEP)*
- Essa aplicação deverá gerar um relatório em CSV com todos os campos de todas as pessoas cadastradas.
- Todas as requisições deverão ter um registro para auditoria posterior.
- A geração do relatório deverá ser executada de forma assíncrona.

## Requisitos Técnicos

As seguintes tecnologias deverão ser consideradas:

### Frontend
- React
- Material UI
- Styled-Components
- Axios
- Typescript *(opcional)*

### Backend
- Java
- JUnit 5
- Spring Boot
- Flyway
- Docker
- PostgreSQL
- RabbitMQ

## Infraestrutura / Ambiente de Desenvolvimento

- Devem ser desenvolvidos testes de unidade para todas as rotinas.
- O projeto deverá ser no GitHub com o repositório público.
- O projeto deverá conter um arquivo de `docker-compose.yml` para qualquer componente utilizado pela aplicação (**Postgres, RabbitMQ, Redis**, etc.).
- As aplicações de frontend e backend deverão ser publicadas em uma imagem no Docker Hub e o projeto deve conter o Dockerfile das aplicações.

---

ViaCEP - Webservice CEP e IBGE gratuito
