CREATE TABLE pessoa (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(50),
    cpf VARCHAR(14) NOT NULL UNIQUE
);

CREATE TABLE endereco (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(50),
    complemento VARCHAR(255),
    cep VARCHAR(10),
    bairro VARCHAR(255),
    municipio VARCHAR(255),
    estado VARCHAR(2),
    pessoa_id BIGINT UNIQUE,
    CONSTRAINT fk_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);