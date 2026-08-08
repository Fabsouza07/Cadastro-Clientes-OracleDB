# Sistema Cadastro Clientes — Oracle Database

Projeto local e educacional em Java 25, Maven e JDBC.

## Preparação
1. Como `SYS AS SYSDBA`, execute `sql/criar_usuario.sql` para criar o usuário `CADASTRO_CLIENTES` no PDB `FREEPDB1`.
2. Conecte-se como `CADASTRO_CLIENTES` e execute `sql/criar_banco.sql`.
3. Edite `src/main/resources/config.properties` com a URL, o usuário e a senha do Oracle. O padrão aponta para Oracle Free no serviço `FREEPDB1` (`localhost:1521`).
   Para outro serviço, use o formato `jdbc:oracle:thin:@//servidor:porta/nome_do_servico`.
4. No terminal da pasta, execute: `mvn clean compile exec:java`.

Seu JDK 26 pode compilar o projeto porque o Maven usa `release 25`.

## Recursos
CRUD completo, localização por ID, nome, cidade ou e-mail, confirmação de alteração/exclusão/saída, ordenação, estatísticas, exportação CSV, validações e interface de console padronizada.
