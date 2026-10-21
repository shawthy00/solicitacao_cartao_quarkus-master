SELECT id,nome,email,criado_em FROM usuarios ORDER BY id;
SELECT id,nome,descricao,disponivel FROM tipos_cartao ORDER BY nome;
SELECT s.id,u.nome AS usuario,t.nome AS tipo_cartao,s.status,s.solicitada_em
FROM solicitacoes_cartao s JOIN usuarios u ON u.id=s.usuario_id
JOIN tipos_cartao t ON t.id=s.tipo_cartao_id ORDER BY s.id;
SELECT c.id,c.solicitacao_id,u.nome AS titular,t.nome AS tipo_cartao,c.status,c.emitido_em
FROM cartoes c JOIN solicitacoes_cartao s ON s.id=c.solicitacao_id
JOIN usuarios u ON u.id=s.usuario_id JOIN tipos_cartao t ON t.id=s.tipo_cartao_id ORDER BY c.id;
