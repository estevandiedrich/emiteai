-- Criação das tabelas de auditoria do Hibernate Envers
-- Essas tabelas são criadas automaticamente pelo Envers, mas definimos explicitamente para controle

-- Tabela de informações de revisão
CREATE TABLE IF NOT EXISTS revinfo (
    rev INTEGER NOT NULL,
    revtstmp BIGINT,
    PRIMARY KEY (rev)
);

-- Sequência para geração de números de revisão
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

-- Tabela de auditoria para Pessoa
CREATE TABLE IF NOT EXISTS pessoa_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    cpf VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Tabela de auditoria para Endereco
CREATE TABLE IF NOT EXISTS endereco_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    numero VARCHAR(255),
    complemento VARCHAR(255),
    cep VARCHAR(255),
    bairro VARCHAR(255),
    municipio VARCHAR(255),
    estado VARCHAR(255),
    pessoa_id BIGINT,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Índices para melhorar performance das consultas de auditoria
CREATE INDEX IF NOT EXISTS idx_pessoa_aud_rev ON pessoa_aud(rev);
CREATE INDEX IF NOT EXISTS idx_pessoa_aud_revtype ON pessoa_aud(revtype);
CREATE INDEX IF NOT EXISTS idx_endereco_aud_rev ON endereco_aud(rev);
CREATE INDEX IF NOT EXISTS idx_endereco_aud_revtype ON endereco_aud(revtype);
CREATE INDEX IF NOT EXISTS idx_revinfo_timestamp ON revinfo(revtstmp);

-- Comentários nas tabelas
COMMENT ON TABLE revinfo IS 'Tabela de revisões do Hibernate Envers';
COMMENT ON TABLE pessoa_aud IS 'Histórico de auditoria da entidade Pessoa';
COMMENT ON TABLE endereco_aud IS 'Histórico de auditoria da entidade Endereco';

COMMENT ON COLUMN revinfo.rev IS 'Número sequencial da revisão';
COMMENT ON COLUMN revinfo.revtstmp IS 'Timestamp da revisão em milissegundos';

COMMENT ON COLUMN pessoa_aud.revtype IS 'Tipo de operação: 0=INSERT, 1=UPDATE, 2=DELETE';
COMMENT ON COLUMN endereco_aud.revtype IS 'Tipo de operação: 0=INSERT, 1=UPDATE, 2=DELETE';
