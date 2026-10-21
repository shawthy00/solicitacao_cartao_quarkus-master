-- Execute UMA VEZ conectado ao banco postgres, fora de uma transação.
-- Não é executado pela API: ela precisa de uma database existente para conectar.
CREATE DATABASE solicitacao_cartoes ENCODING 'UTF8';
