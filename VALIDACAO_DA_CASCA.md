# Validação da estrutura entregue

Verificado em 20/10/2026 com JDK 25.0.2 e Quarkus 3.40.1.

- Compilação e empacotamento concluídos, com as classes e recursos entregues.
- API iniciou com H2 em memória e servidor TCP local; carga: 2 usuários, 3 tipos, 3 solicitações e 1 cartão.
- API iniciou com perfil postgres em PostgreSQL 18 temporário e isolado, com a mesma quantidade de registros.
- Reexecução da carga no PostgreSQL não duplicou os dados.
- Swagger/OpenAPI acessíveis, com 9 operações e 7 marcadas como protegidas.
- Sete rotas de negócio: 401 sem token; com JWT válido, atingem o TODO e retornam 501.
- Assinatura incorreta e JWT expirado rejeitados com 401.
- Cadastro com entrada inválida rejeitado com 400; cadastro/login válidos chegam ao TODO.
- Senha de entrada documentada como writeOnly, sem campo equivalente em UsuarioResponse.
- O hash da carga foi gerado e conferido com BcryptUtil, custo 12.
- Maven test executa, mas há **zero testes implementados**: as três classes de teste estão vazias.

Esta verificação cobre a casca, configuração e carga. Não comprova regras de negócio,
queries dos repositories, cadastro, comparação de senha ou emissão pela rota de login:
essas implementações pertencem ao grupo e continuam como TODO. Não há gabarito oculto.

O PostgreSQL temporário usado na verificação não é o PostgreSQL local da turma nem altera suas credenciais.
