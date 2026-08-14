# Sistema Cadastro Clientes — Oracle Database

Projeto local e educacional em Java 25, Maven e JDBC.

## Preparação
1. Como `SYS AS SYSDBA`, execute `sql/criar_usuario.sql` para criar `CADASTRO_CLIENTES` (proprietário do schema) e `CADASTRO_APP` (conta da aplicação) no PDB `FREEPDB1`.
2. Conecte-se como `CADASTRO_CLIENTES` e execute `sql/criar_banco.sql`, `sql/criar_usuarios.sql` e `sql/conceder_permissoes_app.sql`.
3. Configure `DB_URL`, `DB_USUARIO`, `DB_SENHA` e `DB_SCHEMA` como variáveis de ambiente. Use `CADASTRO_APP` para a aplicação e `CADASTRO_CLIENTES` como schema. Como alternativa local, copie `src/main/resources/config.properties.example` para `config.properties` e preencha a senha. Esse arquivo não deve ser enviado ao Git.
   Para outro serviço, use o formato `jdbc:oracle:thin:@//servidor:porta/nome_do_servico`.

   **Segurança:** mantenha `config.properties.example` no repositório como modelo apenas, e nunca versiona `config.properties` com credenciais reais. Use `config.properties` somente localmente e adicione-o ao `.gitignore` se ainda não estiver ignorado.

   Exemplo de variáveis de ambiente no Windows PowerShell:
   ```powershell
   $env:DB_URL = 'jdbc:oracle:thin:@//localhost:1521/FREEPDB1'
   $env:DB_USUARIO = 'cadastro_app'
   $env:DB_SENHA = 'sua_senha_segura'
   $env:DB_SCHEMA = 'cadastro_clientes'
   ```

   Exemplo em Bash:
   ```bash
   export DB_URL='jdbc:oracle:thin:@//localhost:1521/FREEPDB1'
   export DB_USUARIO='cadastro_app'
   export DB_SENHA='sua_senha_segura'
   export DB_SCHEMA='cadastro_clientes'
   ```
4. Crie o primeiro usuário autenticado executando `criar_usuario.bat` e escolhendo a opção `1`. Informe um login, nome e uma senha forte; a aplicação grava apenas o hash BCrypt.
5. Inicie o sistema com `mvn clean compile exec:java` ou `executar.bat`.

Seu JDK 26 pode compilar o projeto porque o Maven usa `release 25`.

## Recursos
CRUD completo, localização por ID, nome, cidade ou e-mail, autenticação com BCrypt, confirmação de alteração/exclusão/saída, ordenação, estatísticas, exportação CSV, validações e interface de console padronizada.
