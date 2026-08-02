# Sistema Cadastro Clientes — SQL Server 2025

Projeto local e educacional em Java 25, Maven e JDBC.

## Preparação
1. Execute `sql/criar_banco.sql` no SQL Server 2025.
2. Edite `src/main/resources/config.properties` com usuário e senha locais.
3. No terminal da pasta, execute: `mvn clean compile exec:java`.

Seu JDK 26 pode compilar o projeto porque o Maven usa `release 25`.

## Recursos
CRUD completo, confirmação de alteração/exclusão/saída, pesquisa, ordenação, estatísticas, exportação CSV, validações e interface de console padronizada.
