-- Opcional e MANUAL, nunca executado no startup.
-- Primeiro cadastre pela API usuario com e-mail aluno@example.com e implemente BCrypt.
-- Sem esse usuário, os INSERT SELECT inserem zero linhas, sem inventar senha em texto.
-- Não executem enquanto estiverem verificando a regra de duplicidade.
INSERT INTO solicitacoes_cartao (usuario_id,tipo_cartao_id,solicitada_em,status)
SELECT u.id,t.id,CURRENT_TIMESTAMP,'PENDENTE' FROM usuarios u CROSS JOIN tipos_cartao t
WHERE u.email='aluno@example.com' AND t.nome='ESSENCIAL'
AND NOT EXISTS (SELECT 1 FROM solicitacoes_cartao s WHERE s.usuario_id=u.id AND s.tipo_cartao_id=t.id AND s.status='PENDENTE');

INSERT INTO solicitacoes_cartao (usuario_id,tipo_cartao_id,solicitada_em,status)
SELECT u.id,t.id,CURRENT_TIMESTAMP,'EMITIDA' FROM usuarios u CROSS JOIN tipos_cartao t
WHERE u.email='aluno@example.com' AND t.nome='VIAGEM'
AND NOT EXISTS (SELECT 1 FROM solicitacoes_cartao s WHERE s.usuario_id=u.id AND s.tipo_cartao_id=t.id AND s.status='EMITIDA');

INSERT INTO cartoes (solicitacao_id,emitido_em,status)
SELECT s.id,CURRENT_TIMESTAMP,'ATIVO' FROM solicitacoes_cartao s JOIN usuarios u ON u.id=s.usuario_id
WHERE u.email='aluno@example.com' AND s.status='EMITIDA'
AND NOT EXISTS (SELECT 1 FROM cartoes c WHERE c.solicitacao_id=s.id);
