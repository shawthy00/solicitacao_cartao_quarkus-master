# Desafio Solicitação de Cartões

Projeto-base de exercício, inspirado na agência bancária Quarkus. JDK **25.0.2**, Quarkus **3.40.1**, Maven, JPA com EntityManager, JDBC, JWT HS256 e BCrypt.

**Não é um gabarito.** A aplicação compila e inicia, prepara o banco e exibe o Swagger. As regras, consultas, geração de token e hash ainda são TODOs. Uma chamada válida chega ao use case e retorna **501 TODO** até o grupo implementar. Não remover a proteção para contornar o login incompleto.

O enunciado para os alunos está em `../documentos/Desafio_Solicitacao_de_Cartoes.docx`.

## História e escopo

A agência fictícia Aurora quer permitir que seus clientes solicitem um cartão, acompanhem o pedido e simulem a emissão. Cada usuário só consulta e altera seus próprios pedidos.

Não é um produto bancário real. Não há análise de crédito, aprovação administrativa, pagamento, número de cartão, CVV, bandeira ou integração externa. A emissão pelo próprio titular é uma simplificação pedagógica.

## O que está pronto e o que implementar

| Pronto | Trabalho do grupo |
| --- | --- |
| 5 controllers, 9 rotas, segurança declarativa e contratos Swagger | Implementar os 5 use cases |
| 9 DTOs, inclusive cadastro, login e token | Declarar métodos nas 5 interfaces de persistência |
| 4 entidades anotadas, atributos, construtores, getters e setters | Implementar os 5 adaptadores de persistência |
| Estado inicial nos construtores | Preencher comportamentos com TODO nas entidades |
| Tratamento global e exceções comuns | Implementar JwtService e SenhaBCrypt |
| POM, verificador JWT, H2, perfil PostgreSQL e SQL de startup | Migrar as camadas para Clean Architecture |
| Estrutura de teste sem testes resolvidos | Verificar os cenários do desafio |

## Rodar sem Docker

Dentro desta pasta:

```bash
java -version
./mvnw quarkus:dev
```

No Windows PowerShell: `./mvnw.cmd quarkus:dev`. JDK homologado: 25.0.2. No primeiro uso, Maven precisa de internet para baixar dependências.

- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui/
- OpenAPI: http://localhost:8082/openapi

O **H2 é o padrão**, em memória: a aplicação cria tabelas e carga; os dados se perdem quando o processo termina. Ela também abre um servidor TCP H2 em **9093**, apenas para acesso local.

Conexão no DBeaver ou IntelliJ:
- Driver H2; URL: `jdbc:h2:tcp://localhost:9093/mem:cartoes`
- Usuário: `sa`; senha vazia.
- Conectar **depois** que a API iniciar. Não usar outra conexão `jdbc:h2:mem:cartoes` fora do processo: seria outro banco.
- Não há console web H2 neste exercício. PostgreSQL/pgAdmin não se conectam a H2.
- Se a porta estiver ocupada: `./mvnw quarkus:dev -Dapp.h2.tcp.port=9094`; ajuste a URL do cliente.
- Para não abrir TCP: `-Dapp.h2.tcp.enabled=false`. O H2 embutido continua funcionando.

### PostgreSQL local

A API **não instala nem inicia um servidor PostgreSQL**. Inicie seu PostgreSQL local. No pgAdmin ou DBeaver, conectado ao banco `postgres`, execute **isoladamente**, com autocommit, o arquivo `sql/00_criar_database_postgres.sql`. Não execute CREATE DATABASE dentro de BEGIN.

Depois execute:

```bash
./mvnw quarkus:dev -Dquarkus.profile=postgres
```

Padrão do perfil: `jdbc:postgresql://localhost:5432/solicitacao_cartoes`, usuário `postgres`, senha `postgres`. Ajuste no arquivo `src/main/resources/application.properties` ou por ambiente:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/solicitacao_cartoes'
export DB_USER='postgres'
export DB_PASSWORD='sua-senha-local'
./mvnw quarkus:dev -Dquarkus.profile=postgres
```

No PowerShell: `$env:DB_URL='jdbc:postgresql://localhost:5432/solicitacao_cartoes'`, `$env:DB_USER='postgres'`, `$env:DB_PASSWORD='sua-senha-local'`.

**Escolha um banco por execução.** H2 e PostgreSQL não são réplicas nem recebem gravações simultâneas. No perfil PostgreSQL, o TCP H2 fica desabilitado. Compile novamente com o perfil adequado ao empacotar: `./mvnw package -Dquarkus.profile=postgres`.

### SQL pronto

1. `sql/00_criar_database_postgres.sql`: manual, somente PostgreSQL, uma vez.
2. `src/main/resources/db/01_criar_tabelas.sql`: automático no startup.
3. `src/main/resources/db/02_carga_inicial.sql`: automático no startup, sem redefinir registros existentes.
4. `sql/03_carga_relacionamentos_opcional.sql`: manual, opcional, depende do cadastro aluno@example.com.
5. `sql/04_consultas_conferencia.sql`: SELECTs para verificar os relacionamentos.

O `DatabaseInitializer` executa 01 e 02 no banco ativo, em transação. Não usa Hibernate para criar tabelas e não precisa de Docker. É um inicializador de laboratório, **não** um mecanismo de evolução de esquema em produção. IF NOT EXISTS não corrige tabelas antigas.

Carga em uma base vazia: **2 usuários, 3 tipos, 3 solicitações e 1 cartão**. Usuários fictícios:
- `ana@example.com`
- `bruno@example.com`
- Senha pública de ambos: `Cartoes@123` (hash BCrypt custo 12 na carga).
- A senha só funcionará na rota depois de o grupo implementar login e BCrypt.

A carga usa e-mail/nome para relacionar os registros e não presume IDs. Use um novo usuário no roteiro do grupo para não disputar pedidos da demonstração.

## As nove rotas

Todas retornam JSON. Use `Authorization: Bearer <accessToken>` nas sete rotas protegidas.

| Nº | Método e rota | Acesso | Persistência | Sucesso |
| --- | --- | --- | --- | --- |
| 1 | POST /api/usuarios | Pública | JPA | 201 |
| 2 | POST /api/auth/login | Pública | JPA na consulta de usuário | 200 |
| 3 | GET /api/tipos-cartao | Protegida | **JDBC**, única rota | 200 |
| 4 | POST /api/solicitacoes | Protegida | JPA | 201 |
| 5 | GET /api/solicitacoes?status=PENDENTE | Protegida | JPA com JPQL própria | 200 |
| 6 | GET /api/solicitacoes/{id} | Protegida | JPA | 200 |
| 7 | PATCH /api/solicitacoes/{id}/cancelar | Protegida | JPA | 200 |
| 8 | POST /api/solicitacoes/{id}/emitir | Protegida | JPA | 201 |
| 9 | GET /api/cartoes | Protegida | JPA com JPQL própria | 200 |

Sem token, token inválido ou expirado: **401**. Não há perfis administrador/cliente. Autenticação é comum; autorização por **titularidade** deve ser implementada no use case/repository.

Pedidos inexistentes **e** de outro titular retornam **404**. Não aceitar usuarioId no JSON: ele vem do claim assinado. Listas sem resultados retornam 200 com `[]`.

## Entidades e comportamentos

Quatro entidades, sem entidade adicional de endereço ou item.

| Entidade | Propriedades, incluindo ID | Comportamentos TODO |
| --- | --- | --- |
| Usuario | id Long, nome String, email String, senhaHash String, criadoEm Instant | alterarNome; atualizarSenhaHash |
| TipoCartao | id Long, nome String, descricao String, disponivel boolean | alterarDisponibilidade |
| SolicitacaoCartao | id Long, usuario Usuario, tipoCartao TipoCartao, solicitadaEm Instant, status StatusSolicitacao | cancelar; marcarEmitida |
| Cartao | id Long, solicitacao SolicitacaoCartao, emitidoEm Instant, status StatusCartao | estaAtivo |

Usuario 1:N SolicitacaoCartao; TipoCartao 1:N SolicitacaoCartao; SolicitacaoCartao 1:0..1 Cartao. As FKs ficam nos lados de solicitação e cartão. A FK solicitacao_id do cartão é UNIQUE.

Os construtores de cadastro já atribuem propriedades: usuário recebe hash, solicitação nasce PENDENTE e cartão nasce ATIVO. Eles não implementam as validações que faltam. O construtor protegido sem argumentos existe para o JPA.

Os setters foram entregues para acompanhar o padrão da turma, mas o use case **não pode** usá-los para contornar cancelar/marcarEmitida. Senha e hash nunca saem nos DTOs de resposta.

### Regras obrigatórias

1. Cadastro: nome não vazio, e-mail válido e único após trim/minúsculas; senha de 8 a 72 caracteres e no máximo 72 **bytes UTF-8**. Salvar BCrypt, nunca texto.
2. Login: buscar e-mail normalizado; usar matches; erro genérico 401 tanto para usuário ausente quanto para senha errada.
3. Tipos: listar apenas disponíveis, por nome, via JDBC.
4. Criar pedido: titular precisa existir, tipo precisa existir (404) e estar disponível (409).
5. Não permitir outro pedido **PENDENTE** para o mesmo titular e tipo (409). Depois de cancelado ou emitido, uma nova solicitação é permitida.
6. Listar e detalhar: retornar somente registros do usuário autenticado. Status inválido na query gera 400.
7. Cancelar: somente PENDENTE vira CANCELADA. Cancelar novamente ou cancelar EMITIDA gera 409.
8. Emitir: somente PENDENTE, do próprio titular. Criar cartão ATIVO e alterar pedido para EMITIDA na **mesma transação**. Erro em qualquer gravação deve desfazer ambas.
9. Uma solicitação só pode gerar um cartão. Repetir emissão gera 409.
10. Cartões: listar somente os próprios, trazendo nome do tipo por relacionamento, sem expor entidade inteira.

Para concorrência, usem bloqueio de escrita da solicitação ao emitir e cancelar, dentro da transação. Para impedir dois cadastros pendentes simultâneos, uma opção didática é bloquear o usuário antes de conferir duplicidade e inserir. O SQL já impede dois cartões para a mesma solicitação; tratem a violação esperada sem vazar SQL. Um catch genérico não deve esconder falhas desconhecidas como 409.

## Ordem de implementação

O pacote-base é `br.edu.fiap.cartoes`. Não alterem o projeto da agência bancária.

1. **Entity**: completar cancelar e marcarEmitida em SolicitacaoCartao; testar transições. Preencher os comportamentos de Usuario, TipoCartao e Cartao.
2. **Interfaces de repository**: declarar operações nas cinco interfaces que os use cases precisam. Não há geração automática por nomes como countBy: aqui é EntityManager e implementação manual.
3. **UsuarioRepositoryJpa**: salvar, buscar por e-mail e por ID; parametrizar consultas.
4. **SenhaBCrypt**: implementar criarHash e confere com BcryptUtil, custo configurado e limite em bytes.
5. **UsuarioUseCase.cadastrar**: validar, normalizar, verificar unicidade, gerar hash, persistir em transação, mapear UsuarioResponse.
6. **JwtService.emitir**: montar iss, sub=email, iat, exp e usuarioId; assinar HS256 com segredo configurado; preencher TokenResponse.
7. **AutenticacaoUseCase.login**: buscar usuário, comparar hash e emitir token; não confundir hash com senha.
8. **TipoCartaoRepositoryJdbc e TipoCartaoUseCase**: implementar listarDisponiveis com PreparedStatement e try-with-resources.
9. **TipoCartaoRepositoryJpa e SolicitacaoCartaoRepositoryJpa**: buscar tipo por ID; salvar pedido, consultar por ID e titular, listar por titular/status e verificar pedido pendente.
10. **SolicitacaoUseCase.criar, listar e buscar**: aplicar acesso e regras usando o repositório.
11. **SolicitacaoUseCase.cancelar**: abrir transação, buscar/bloquear pedido próprio e usar o comportamento da entidade.
12. **CartaoRepositoryJpa**: salvar, verificar vínculo único e listar por titular com o tipo.
13. **SolicitacaoUseCase.emitir e CartaoUseCase.listar**: compor emissão atômica e consulta relacionada.
14. Conferir Swagger, JSON e tabelas no banco ativo; executar os cenários de aceitação.
15. Migrar para Clean Architecture mantendo exatamente os contratos HTTP e as regras.

## Contratos que o grupo precisa desenhar

Os nomes abaixo são uma orientação de assinatura, não implementações prontas.

| Interface atual | Operações esperadas |
| --- | --- |
| UsuarioRepository | salvar, buscarPorEmail, buscarPorId; opção de busca com bloqueio para serializar novos pedidos |
| ConsultaTiposDisponiveis | listarDisponiveis via JDBC |
| TipoCartaoRepository | buscarPorId via JPA |
| SolicitacaoCartaoRepository | salvar, buscarPorIdETitular, listarPorTitularEStatus, existePendente; busca com bloqueio para transições |
| CartaoRepository | salvar, existePorSolicitacao, listarPorTitular |

**Atenção ao tipo de cartão:** `ConsultaTiposDisponiveis` tem somente a listagem e é implementada por `TipoCartaoRepositoryJdbc`. `TipoCartaoRepository` é o contrato da busca por ID e é implementado por `TipoCartaoRepositoryJpa`. Assim, só a rota GET usa JDBC; a criação de solicitação continua inteiramente JPA, sem ambiguidade de injeção.

### O que observar nas queries

- JDBC: SQL usa tabelas e colunas; obter conexão do Agroal, bind de disponivel, ordenar por nome e fechar Connection/PreparedStatement/ResultSet.
- JPA: JPQL usa **nomes de classes e atributos**, não nomes de tabelas. Buscar a solicitação com o tipo usando relacionamento.
- Listagem de solicitações: filtro obrigatório de usuario.id e opcional de status; ordenar por solicitadaEm e id.
- Listagem de cartões: navegar cartao.solicitacao.usuario e cartao.solicitacao.tipoCartao; incluir somente o titular atual.
- Não concatenar entrada do cliente em SQL/JPQL. Usar parâmetros nomeados/posicionais.
- Mapear DTO enquanto a transação de leitura está ativa ou carregar os relacionamentos explicitamente. Não depender de serializar proxies LAZY.

## Segurança preparada

Os controllers protegidos usam `@Authenticated`; cadastro e login usam `@PermitAll`. `UsuarioAtual` lê usuarioId de um token verificado. A configuração rejeita JWT com assinatura/issuer/expiração inválidos.

O grupo implementa emissão e BCrypt, não um filtro JWT manual. Configurações de custo 12 e validade 900 segundos são escolhas do laboratório, não números obrigatórios para toda produção.

No Swagger, execute o login, copie só o accessToken e cole em **Authorize**. A UI monta o Bearer. Use HTTPS em ambiente real. A chave de desenvolvimento no properties e no jwt-secret.jwk é pública e não deve ser reutilizada fora da aula.

O JWK pronto guarda os mesmos bytes do segredo em Base64URL. Se alterar o segredo de assinatura por JWT_SECRET, forneça também um JWK correspondente via JWT_VERIFY_KEY_LOCATION. Alterar só um lado faz o token ser rejeitado. O laboratório já vem com o par coerente, sem exigir alteração para começar.

## Migração obrigatória para Clean Architecture

A casca começa em camadas convencionais. O grupo implementa e depois refatora sem mudar as rotas:

```text
domain/model                  comportamentos e estados
application/usecase           coordenação das operações
application/port/out          contratos de persistência, hash e token
application/model             entradas e resultados sem tipos HTTP
infrastructure/persistence    JDBC, JPA e mapeamentos
infrastructure/security       BCrypt e JWT SmallRye
infrastructure/config         banco, bootstrap e configuração
presentation/controller       recursos HTTP
presentation/dto              contratos JSON/Swagger
presentation/exception        conversão de erros para HTTP
```

Não basta mover pastas: use cases dependem de interfaces internas, não de classes JDBC/JPA/JWT/BCrypt concretas; o controller converte o DTO para a entrada da aplicação. O domínio não importa controller ou DTO. Os adaptadores implementam as portas.

Para manter o esforço adequado, **não é obrigatório duplicar todas as entidades**: o grupo pode manter as anotações JPA no modelo como concessão didática, registrando essa decisão. Isso não é isolamento estrito de persistência. Quem escolher isolamento estrito cria modelo JPA e mapeadores próprios, sem criar novas entidades de negócio.

Comprovar antes/depois pelo mesmo requests.http e um desenho das dependências. Não usar a migração para inserir SQL no use case ou regra no controller.

## Roteiro de aceitação

Use um usuário novo para evitar colisões com os dados da carga.

1. Cadastrar aluno@example.com e outro usuário; retorno 201 sem senha/hash.
2. Login errado retorna 401; login correto retorna token Bearer.
3. Sem token, GET tipos retorna 401. Com token, retorna ESSENCIAL e VIAGEM, não EMPRESARIAL.
4. Criar pedido para tipo disponível retorna PENDENTE; repetir gera 409; tipo indisponível gera 409 e tipo inexistente gera 404.
5. Listar por status e detalhar o pedido; o token do segundo usuário não deve enxergar nem alterar o pedido do primeiro (404 no detalhe).
6. Cancelar: PENDENTE -> CANCELADA; repetir gera 409.
7. Criar outro pedido e emitir: cartão ATIVO e solicitação EMITIDA; repetir gera 409, sem segundo cartão.
8. Listar cartões do titular, conferir IDs e FKs nos SELECTs.
9. Forçar falha na emissão durante desenvolvimento e demonstrar rollback; remover qualquer gancho artificial antes de entregar.
10. Após a migração Clean, repetir o roteiro sem mudar JSONs e rotas.

Estrutura de testes pronta, sem exercícios resolvidos: `src/test/java`. Como evidência adicional, sugerimos dois testes unitários (cancelamento permitido e emissão em estado inválido) e um de integração (401 sem token). O projeto não possui cobertura de regra de negócio enquanto esses testes não forem escritos.

## Definição de pronto

- Nove rotas conforme contrato; uma rota JDBC e as demais JPA, sem implementar novos endpoints.
- Comportamentos preenchidos; nenhuma chamada obrigatória permanece em 501.
- Hash BCrypt salvo e ausente das respostas; login e verificação JWT funcionando.
- Sete rotas protegidas e acesso restrito ao titular.
- Emissão atômica, estados válidos, duplicidade tratada e exceções coerentes.
- Consultas JPQL customizadas e parâmetros JDBC demonstrados.
- Relações conferidas no banco; dois usuários usados no teste de isolamento.
- Clean Architecture com portas e dependências explicadas; concessões registradas.
- Swagger coerente e requests.http atualizado com IDs reais, sem commitar tokens.
- README atualizado pelo grupo com as decisões de implementação.

## Referências oficiais

- Quarkus ORM/JPA: https://quarkus.io/guides/hibernate-orm
- Datasources: https://quarkus.io/guides/datasource
- JWT: https://quarkus.io/guides/security-jwt e https://quarkus.io/guides/security-jwt-build
- OpenAPI: https://quarkus.io/guides/openapi-swaggerui
- BCrypt: BcryptUtil, módulo quarkus-elytron-security-common (mesmo da agência).
