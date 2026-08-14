# Análise Geral do Projeto — Sistema Cadastro Clientes

## Resumo

Projeto Java (Maven) que implementa um sistema de cadastro de clientes com backend JDBC para Oracle Database. A interface principal é Swing (GUI), há utilitários de console, exportação CSV e autenticação com BCrypt. Existe um projeto SQL (`cadastro_clientes.sqlproj`) e scripts em `sql/` para criar usuários e schema.

## Estrutura principal

- **Fonte:** `src/main/java` — separação por pacotes `app`, `dao`, `service`, `model`, `util`.
- **Recursos:** `src/main/resources/config.properties` (exemplo presente).
- **Infra:** `sql/` com scripts de criação de schema/usuários e `cadastro_clientes/` (sqlproj) para deployment Oracle.
- **Build:** `pom.xml` (Maven) targeting Java release 25.
- **Testes:** relatórios em `target/surefire-reports/` indicam testes unitários JUnit 5 com Mockito.

## O que foi verificado

- `README.md`: contém instruções de preparação e execução local (`mvn clean compile exec:java`), recomenda uso de variáveis de ambiente.
- `pom.xml`: dependência `ojdbc11`, JUnit 5, Mockito, plugin `exec-maven-plugin` configurado com `br.com.cadastro.app.Main`.
- `config.properties`: exemplo com `db.senha` preenchida — atenção a segredos em arquivos de projeto.
- `src/main/java/br/com/cadastro/app/Main.java`: inicialização Swing e checagem de conexão com `Conexao.testar()`; tratamento de erros apresenta diálogo e finaliza aplicacão.
- Testes: relatório do Surefire mostra testes de validação em `ClienteService` (sem falhas no último run).

## Pontos fortes

- Estrutura de pacotes organizada e coesa (DAO/Service/Model/Util).
- Testes automatizados presentes e integrados ao Maven/Surefire.
- Uso de BCrypt para hashes de senha (boa prática de segurança).
- Scripts SQL e projeto `.sqlproj` facilitam provisionamento Oracle.

## Riscos / pontos a melhorar

- `config.properties` exemplo contém senha em texto — evitar commitar credenciais. Prefira apenas `config.properties.example` e variáveis de ambiente.
- Dependência Oracle JDBC é proprietária; documentar como obter/instalar no repositório privado ou instruções no README.
- Falta CI/CD (build automático, execução de testes, scanner estático).
- Ausência de containerização (Docker) para ambiente consistente; importante ao integrar com Oracle em CI.
- Cobertura de testes: os relatórios indicam testes de validação, mas é recomendado adicionar testes de integração com banco (ou usar Testcontainers/mocks apropriados).

## Recomendações prioritárias

1. Remover senhas do repositório e instruir uso de variáveis de ambiente ou `secret manager`.
2. Adicionar um `README.md` seção *Build & Run* com comandos explícitos e pré-requisitos (JDK, Oracle XE/connection details).
3. Incluir configuração mínima de CI (GitHub Actions / Azure Pipelines) para `mvn -DskipTests=false test` e `mvn package`.
4. Considerar Docker + profile de desenvolvimento (container para a aplicação e/ou instruções para conectar a um Oracle local).
5. Avaliar uso de um mecanismo de migrações (Flyway/Liquibase) para versionar scripts SQL.

## Observações finais

Projeto está bem organizado e adequado para uso pedagógico ou POC. Com pequenas melhorias em segurança e automação, fica pronto para implantação mais confiável em ambientes controlados.

---

Arquivo gerado automaticamente: `ANALISE_PROJETO.md`.
