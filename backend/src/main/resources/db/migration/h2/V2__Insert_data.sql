-- Migration: V2__Insert_data.sql
-- Description: Inserção de dados de exemplo para testes e demonstração

-- Inserir responsáveis de exemplo
INSERT INTO responsaveis (nome, email, cargo) VALUES
  ('João Silva', 'joao.silva@empresa.com', 'Desenvolvedor Senior'),
  ('Maria Santos', 'maria.santos@empresa.com', 'Product Owner'),
  ('Pedro Oliveira', 'pedro.oliveira@empresa.com', 'Scrum Master'),
  ('Ana Costa', 'ana.costa@empresa.com', 'Desenvolvedor Pleno'),
  ('Carlos Lima', 'carlos.lima@empresa.com', 'Desenvolvedor Junior')
;

-- Inserir projetos de exemplo
INSERT INTO projetos (nome, status, inicio_previsto, termino_previsto, inicio_realizado, dias_atraso, percentual_tempo_restante) VALUES
  ('Sistema de Gestão Comercial', 'EM_ANDAMENTO', '2024-01-15', '2024-06-15', '2024-01-20', 0, 65.5),
  ('Portal do Cliente', 'A_INICIAR', '2024-02-01', '2024-05-01', NULL, 0, 100.0),
  ('Aplicativo Mobile', 'ATRASADO', '2024-01-01', '2024-03-01', NULL, 25, 0.0),
  ('Migração para Cloud', 'CONCLUIDO', '2023-11-01', '2024-01-15', '2023-11-05', 0, 0.0),
  ('Sistema de Relatórios', 'EM_ANDAMENTO', '2024-02-10', '2024-04-30', '2024-02-12', 0, 78.3)
;

-- Associar responsáveis aos projetos
INSERT INTO projeto_responsavel (projeto_id, responsavel_id) VALUES
  (1, 1), -- João no Sistema de Gestão
  (1, 2), -- Maria no Sistema de Gestão
  (2, 2), -- Maria no Portal do Cliente
  (2, 3), -- Pedro no Portal do Cliente
  (3, 1), -- João no App Mobile
  (3, 4), -- Ana no App Mobile
  (4, 5), -- Carlos na Migração Cloud
  (5, 4), -- Ana no Sistema de Relatórios
  (5, 5)  -- Carlos no Sistema de Relatórios
;