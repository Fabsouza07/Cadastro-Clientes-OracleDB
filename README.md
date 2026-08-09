# Sistema Cadastro Clientes — Oracle Database

Projeto local e educacional em Java 25, Maven e JDBC.

## Preparação
1. Como `SYS AS SYSDBA`, execute `sql/criar_usuario.sql` para criar o schema `CADASTRO_CLIENTES` no PDB `FREEPDB1`.
2. Conecte-se como `CADASTRO_CLIENTES` e execute `sql/criar_banco.sql` e `sql/criar_usuarios.sql`.
3. Configure `DB_URL`, `DB_USUARIO` e `DB_SENHA` como variáveis de ambiente. Como alternativa local, copie `src/main/resources/config.properties.example` para `config.properties` e preencha a senha. Esse arquivo não deve ser enviado ao Git.
   Para outro serviço, use o formato `jdbc:oracle:thin:@//servidor:porta/nome_do_servico`.
4. Crie o primeiro usuário autenticado executando `criar_usuario.bat` e escolhendo a opção `1`. Informe um login, nome e uma senha forte; a aplicação grava apenas o hash BCrypt.
5. Inicie o sistema com `mvn clean compile exec:java` ou `executar.bat`.

Seu JDK 26 pode compilar o projeto porque o Maven usa `release 25`.

## Recursos
CRUD completo, localização por ID, nome, cidade ou e-mail, autenticação com BCrypt, confirmação de alteração/exclusão/saída, ordenação, estatísticas, exportação CSV, validações e interface de console padronizada.
