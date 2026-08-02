# Sistema Cadastro Clientes — Oracle Database

Projeto local e educacional em Java 25, Maven e JDBC.

## Preparação
1. Crie (ou escolha) um usuário/schema Oracle com permissão para criar tabelas.
2. Conecte-se a esse schema e execute `sql/criar_banco.sql`.
3. Edite `src/main/resources/config.properties` com a URL, o usuário e a senha do Oracle. O padrão aponta para a instalação local no serviço `ORCLPDB` (`localhost:1521`).
   Para outro serviço, use o formato `jdbc:oracle:thin:@//servidor:porta/nome_do_servico`.
4. No terminal da pasta, execute: `mvn clean compile exec:java`.

Seu JDK 26 pode compilar o projeto porque o Maven usa `release 25`.

## Recursos
CRUD completo, localização por ID, nome, cidade ou e-mail, confirmação de alteração/exclusão/saída, ordenação, estatísticas, exportação CSV, validações e interface de console padronizada.
