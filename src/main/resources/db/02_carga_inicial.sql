-- Carga repetível e não destrutiva: não redefine senha, status ou dados existentes.
-- Contas fictícias de demonstração abaixo usam hash BCrypt já gerado (custo 12).
-- Senha pública do laboratório para ambas: Cartoes@123. Nunca usar em ambiente real.
INSERT INTO tipos_cartao (nome, descricao, disponivel)
SELECT 'ESSENCIAL', 'Cartao ficticio para compras do dia a dia', TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_cartao WHERE nome='ESSENCIAL');
INSERT INTO tipos_cartao (nome, descricao, disponivel)
SELECT 'VIAGEM', 'Cartao ficticio para planejamento de viagens', TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_cartao WHERE nome='VIAGEM');
INSERT INTO tipos_cartao (nome, descricao, disponivel)
SELECT 'EMPRESARIAL', 'Produto fora de oferta neste desafio', FALSE
WHERE NOT EXISTS (SELECT 1 FROM tipos_cartao WHERE nome='EMPRESARIAL');

INSERT INTO usuarios (nome,email,senha_hash,criado_em)
SELECT 'Ana Lima','ana@example.com','$2a$12$nvZa3SDrisp3.ItyrEFu6eqAgnTQofpNH7Nam5wzbJT53C3suPk.e',CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email='ana@example.com');
INSERT INTO usuarios (nome,email,senha_hash,criado_em)
SELECT 'Bruno Costa','bruno@example.com','$2a$12$nvZa3SDrisp3.ItyrEFu6eqAgnTQofpNH7Nam5wzbJT53C3suPk.e',CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email='bruno@example.com');

-- Dados reservados para demonstração; use um novo usuário no roteiro do grupo.
INSERT INTO solicitacoes_cartao (usuario_id,tipo_cartao_id,solicitada_em,status)
SELECT u.id,t.id,CURRENT_TIMESTAMP,'PENDENTE' FROM usuarios u CROSS JOIN tipos_cartao t
WHERE u.email='ana@example.com' AND t.nome='ESSENCIAL'
AND NOT EXISTS (SELECT 1 FROM solicitacoes_cartao s WHERE s.usuario_id=u.id AND s.tipo_cartao_id=t.id);
INSERT INTO solicitacoes_cartao (usuario_id,tipo_cartao_id,solicitada_em,status)
SELECT u.id,t.id,CURRENT_TIMESTAMP,'EMITIDA' FROM usuarios u CROSS JOIN tipos_cartao t
WHERE u.email='ana@example.com' AND t.nome='VIAGEM'
AND NOT EXISTS (SELECT 1 FROM solicitacoes_cartao s WHERE s.usuario_id=u.id AND s.tipo_cartao_id=t.id);
INSERT INTO solicitacoes_cartao (usuario_id,tipo_cartao_id,solicitada_em,status)
SELECT u.id,t.id,CURRENT_TIMESTAMP,'CANCELADA' FROM usuarios u CROSS JOIN tipos_cartao t
WHERE u.email='bruno@example.com' AND t.nome='VIAGEM'
AND NOT EXISTS (SELECT 1 FROM solicitacoes_cartao s WHERE s.usuario_id=u.id AND s.tipo_cartao_id=t.id);
INSERT INTO cartoes (solicitacao_id,emitido_em,status)
SELECT s.id,CURRENT_TIMESTAMP,'ATIVO' FROM solicitacoes_cartao s JOIN usuarios u ON u.id=s.usuario_id
JOIN tipos_cartao t ON t.id=s.tipo_cartao_id
WHERE u.email='ana@example.com' AND t.nome='VIAGEM' AND s.status='EMITIDA'
AND NOT EXISTS (SELECT 1 FROM cartoes c WHERE c.solicitacao_id=s.id);
