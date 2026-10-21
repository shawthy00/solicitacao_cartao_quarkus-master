# Estrutura de testes

Nenhum teste de negócio está implementado. Os arquivos são classes vazias com TODO.

- entity/SolicitacaoCartaoTest: exercitar cancelar e marcarEmitida sem subir Quarkus.
- usecase/SolicitacaoUseCaseTest: isolar portas com doubles ou Mockito após a migração.
- controller/SegurancaIntegracaoTest: usar @QuarkusTest e RestAssured, com perfil test e H2.

Executar com ./mvnw test. Antes de escrever @Test, a execução pode terminar sem testes: isso não comprova regras nem cobertura.
