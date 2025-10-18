-- Migration: V1__Create__tables.sql
-- Description: Criação das tabelas iniciais do sistema Kanban

-- Tabela de responsáveis
CREATE TABLE responsaveis (
                              id BIGSERIAL PRIMARY KEY,
                              nome VARCHAR(255) NOT NULL,
                              email VARCHAR(255) UNIQUE NOT NULL,
                              cargo VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de projetos
CREATE TABLE projetos (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'A_INICIAR',
                          inicio_previsto DATE,
                          termino_previsto DATE,
                          inicio_realizado DATE,
                          termino_realizado DATE,
                          dias_atraso INTEGER DEFAULT 0,
                          percentual_tempo_restante DOUBLE PRECISION DEFAULT 0.0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de relacionamento projeto_responsavel
CREATE TABLE projeto_responsavel (
                                     projeto_id BIGINT NOT NULL,
                                     responsavel_id BIGINT NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (projeto_id, responsavel_id),
                                     CONSTRAINT fk_projeto FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_responsavel FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id) ON DELETE CASCADE
);