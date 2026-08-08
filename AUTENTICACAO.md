# Autenticação de Usuários - Guia de Implementação

## Visão Geral

O sistema agora inclui autenticação de usuários com segurança implementada através de:

- **Bcrypt**: Hash de senha com cost 12
- **LoginFrame**: Tela de login antes de acessar a aplicação principal
- **UsuarioDAO/UsuarioService**: Camada de acesso e lógica de autenticação
- **UsuarioUtil**: Utilitário para gerenciar usuários

## Configuração Inicial

### 1. Criar a Tabela de Usuários

Execute o script SQL no seu banco Oracle:

```bash
sqlplus cadastro_clientes/NovaSenhaForte2026@FREEPDB1 < sql/criar_usuarios.sql
```

Ou execute manualmente via SQL Developer:

```sql
CREATE TABLE usuarios (
    id           NUMBER GENERATED ALWAYS AS IDENTITY,
    login        VARCHAR2(50) NOT NULL UNIQUE,
    senha        VARCHAR2(255) NOT NULL,
    nome         VARCHAR2(100) NOT NULL,
    ativo        NUMBER(1) DEFAULT 1 NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
```

### 2. Criar Primeiro Usuário

Use o utilitário UsuarioUtil para criar usuários:

```bash
mvn clean compile exec:java -Dexec.mainClass="br.com.cadastro.util.UsuarioUtil"
```

Selecione a opção "1" para criar um novo usuário.

**Exemplo:**
```
Login: admin
Nome completo: Administrador
Senha: admin123
```

## Componentes Adicionados

### 1. LoginFrame (br.com.cadastro.app.LoginFrame)

- Tela de login com interface moderna
- Validação de credenciais
- Tratamento de erros
- Thread-safe (SwingWorker)

**Features:**
- Campo de login
- Campo de senha (mascarado)
- Botão "Entrar"
- Mensagens de erro
- Suporte para Enter key

### 2. Usuario Model (br.com.cadastro.model.Usuario)

Record que representa um usuário:

```java
public record Usuario(
    Long id,
    String login,
    String senha,  // Hash bcrypt
    String nome,
    boolean ativo
)
```

### 3. UsuarioDAO (br.com.cadastro.dao.UsuarioDAO)

Operações de banco de dados para usuários:

- `buscarPorLogin(String login)`: Busca usuário ativo por login
- `criar(Usuario usuario)`: Insere novo usuário
- `atualizarSenha(long id, String novaSenha)`: Atualiza senha
- `desativar(long id)`: Desativa usuário (soft delete)

### 4. UsuarioService (br.com.cadastro.service.UsuarioService)

Lógica de negócio para autenticação:

- `autenticar(String login, String senha)`: Valida credenciais com bcrypt
- `criar(String login, String senha, String nome)`: Cria novo usuário com hash
- `buscarPorLogin(String login)`: Busca usuário
- `atualizarSenha(long id, String senhaAtual, String novaSenha)`: Atualiza senha com validação
- `desativar(long id)`: Desativa usuário

### 5. UsuarioUtil (br.com.cadastro.util.UsuarioUtil)

Utilitário CLI para gerenciar usuários:

```bash
mvn clean compile exec:java -Dexec.mainClass="br.com.cadastro.util.UsuarioUtil"
```

**Operações:**
1. Criar novo usuário
2. Testar autenticação
3. Gerar hash de senha
4. Sair

## Fluxo de Autenticação

```
1. Aplicação inicia (Main.java)
   ↓
2. Exibe SplashScreen
   ↓
3. Testa conexão com banco
   ↓
4. Exibe LoginFrame
   ↓
5. Usuário insere credenciais
   ↓
6. UsuarioService.autenticar() valida senha com bcrypt
   ↓
7. Se válido → Abre MainFrame com dados do usuário
   Se inválido → Mensagem de erro
```

## Segurança

### Bcrypt Configuration

- **Cost Factor**: 12 (padrão, bom balanço segurança/performance)
- **Hashing**: Um-way (impossível recuperar senha original)
- **Salt**: Gerado automaticamente por bcrypt

### Boas Práticas Implementadas

✅ Senhas nunca armazenadas em texto plano  
✅ Validação de entrada  
✅ Soft delete (usuários desativados, não deletados)  
✅ Autenticação thread-safe (SwingWorker)  
✅ Mensagens de erro genéricas (não revelam se login existe)  

## Testes Unitários

Testes inclusos para UsuarioService:

```bash
mvn test -Dtest=UsuarioServiceTest
```

Casos de teste:
- Autenticação com credenciais válidas
- Rejeição de credenciais inválidas
- Validação de entrada
- Criação de usuário com hash
- Atualização de senha

## Dependência Adicionada

```xml
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bcrypt</artifactId>
    <version>0.10.2</version>
</dependency>
```

## Modificações em Classes Existentes

### Main.java

Modificado para exibir LoginFrame ao invés de MainFrame diretamente.

```java
new LoginFrame(usuario -> {
    new MainFrame(usuario).setVisible(true);
}).setVisible(true);
```

### MainFrame.java

- Adicionado campo `Usuario usuarioLogado`
- Construtor agora aceita parâmetro Usuario
- Header exibe nome do usuário autenticado

## Próximas Melhorias Sugeridas

- [ ] Recuperação de senha (email)
- [ ] Dois fatores (2FA)
- [ ] Auditoria de login (logs)
- [ ] Política de senhas (expirações)
- [ ] Controle de acesso (roles/permissões)
- [ ] Bloqueio após tentativas falhas
- [ ] Sessões (timeout)

## Troubleshooting

### Erro: "Arquivo config.properties não encontrado"

Verifique que o arquivo está em `src/main/resources/config.properties`

### Erro: "Tabela USUARIOS não existe"

Execute o script criar_usuarios.sql no banco:

```bash
sqlplus cadastro_clientes/senha@FREEPDB1 < sql/criar_usuarios.sql
```

### "Login ou senha inválidos" mas credenciais estão corretas

- Verifique se o usuário está ativo (coluna `ativo = 1`)
- Confirme que a senha foi criada com UsuarioUtil (não manualmente)
- Verifique case-sensitivity do login (diferencia maiúsculas)

## Exemplos de Uso

### Via Aplicação GUI

```
1. Inicie a aplicação: mvn exec:java
2. Tela de login aparecerá
3. Digite login e senha
4. Clique "Entrar" ou pressione Enter
5. Se correto, MainFrame abrirá
```

### Via UsuarioUtil

```bash
# Build
mvn clean compile

# Executar utilitário
exec:java -Dexec.mainClass="br.com.cadastro.util.UsuarioUtil"

# Opção 1: Criar novo usuário
# Opção 2: Testar autenticação
# Opção 3: Gerar hash de senha
```

### Via Código Java

```java
UsuarioService service = new UsuarioService();

// Criar usuário
long usuarioId = service.criar("joao", "senha123", "João Silva");

// Autenticar
Optional<Usuario> usuario = service.autenticar("joao", "senha123");
if (usuario.isPresent()) {
    System.out.println("Bem-vindo: " + usuario.get().nome());
}
```

---

**Versão**: 2.0.0  
**Data**: 2026-08-08  
**Autor**: Sistema Cadastro Clientes
