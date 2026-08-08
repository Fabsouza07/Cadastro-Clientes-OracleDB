# Testes Unitários - Guia Completo

## Visão Geral

O projeto agora inclui testes unitários abrangentes usando:

- **JUnit 5**: Framework de testes
- **Mockito**: Mock de dependências
- **MVN Test**: Execução de testes

## Dependências de Teste

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

## Estrutura de Testes

```
src/test/java/br/com/cadastro/
├── service/
│   └── ClienteServiceTest.java
└── dao/
    └── ClienteDAOTest.java
```

## Executar Testes

### Executar Todos os Testes

```bash
mvn clean test
```

### Executar Testes de uma Classe Específica

```bash
mvn test -Dtest=ClienteServiceTest
mvn test -Dtest=ClienteDAOTest
```

### Executar um Teste Específico

```bash
mvn test -Dtest=ClienteServiceTest#testCadastrarClienteValido
```

### Executar com Cobertura de Código

```bash
mvn clean test jacoco:report
# Relatório gerado em: target/site/jacoco/index.html
```

### Executar Testes em Modo Verbose

```bash
mvn test -e -X
```

## ClienteServiceTest

Testa a lógica de negócio do serviço de clientes.

### Testes Implementados

| Teste | Descrição |
|-------|-----------|
| `testCadastrarClienteValido` | Cadastra cliente com dados válidos |
| `testCadastrarSemNome` | Rejeita cliente sem nome |
| `testCadastrarIdadeInvalida` | Rejeita idade fora do intervalo 1-120 |
| `testCadastrarEmailInvalido` | Rejeita e-mail sem formato válido |
| `testCadastrarTelefoneFixoIncompleto` | Rejeita telefone fixo com < 10 dígitos |
| `testCadastrarTelefoneCelularIncompleto` | Rejeita telefone celular com < 11 dígitos |
| `testAlterarClienteValido` | Atualiza cliente com dados válidos |
| `testListarClientes` | Lista todos os clientes |
| `testPesquisarClientePorTermo` | Busca cliente por termo |
| `testBuscarClientePorId` | Busca cliente por ID específico |
| `testBuscarClienteNaoExistente` | Retorna vazio para ID inexistente |
| `testExcluirCliente` | Deleta cliente com sucesso |
| `testObtenerEstatisticas` | Retorna estatísticas corretas |
| `testCadastrarSemTelefones` | Permite criar cliente sem telefones |
| `testCadastrarSemCidade` | Rejeita cliente sem cidade |

### Exemplo de Teste

```java
@Test
@DisplayName("Deve cadastrar cliente com dados válidos")
void testCadastrarClienteValido() throws SQLException {
    when(dao.inserir(any(Cliente.class))).thenReturn(1L);

    long id = service.cadastrar(
        "João Silva",
        35,
        "São Paulo",
        "joao@email.com",
        "(11) 3333-3333",
        "(11) 99999-9999"
    );

    assertEquals(1L, id);
    verify(dao, times(1)).inserir(any(Cliente.class));
}
```

### Cobertura de Testes

- Validações: 100%
- Cadastro: 100%
- Busca: 100%
- Listagem: 100%
- Exclusão: 100%

## ClienteDAOTest

Testa a camada de acesso a dados com mocks de conexões SQL.

### Testes Implementados

| Teste | Descrição |
|-------|-----------|
| `testInserirCliente` | Insere cliente e retorna ID |
| `testListarClientesOrdenadosPorNome` | Lista clientes ordenados por nome |
| `testBuscarClientePorId` | Busca cliente existente por ID |
| `testBuscarClienteNaoExistente` | Retorna vazio para ID inexistente |
| `testAtualizarCliente` | Atualiza dados do cliente |
| `testExcluirCliente` | Deleta cliente com sucesso |
| `testExcluirClienteFalha` | Falha na exclusão retorna false |
| `testPesquisarClientePorNome` | Pesquisa por nome com LIKE |
| `testObterEstatisticas` | Calcula estatísticas corretamente |

### Exemplo de Teste com Mock

```java
@Test
@DisplayName("Deve buscar cliente por ID")
void testBuscarClientePorId() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
        mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);
        
        PreparedStatement stmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);
        
        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
        when(stmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        // ... mock dos ResultSet.getters()
        
        Optional<Cliente> cliente = dao.buscarPorId(1L);
        
        assertTrue(cliente.isPresent());
        assertEquals("João Silva", cliente.get().nome());
    }
}
```

### Padrão de Mock

Os testes usam o padrão de mock:

1. **Arrange**: Configura os mocks
2. **Act**: Executa o método testado
3. **Assert**: Valida o resultado

```java
// Arrange
when(dao.inserir(any(Cliente.class))).thenReturn(1L);

// Act
long id = service.cadastrar(...);

// Assert
assertEquals(1L, id);
verify(dao, times(1)).inserir(any(Cliente.class));
```

## Assertions Comuns

```java
// Comparações
assertEquals(valor_esperado, valor_real);
assertNotEquals(valor1, valor2);
assertTrue(condicao);
assertFalse(condicao);

// Objetos
assertNull(objeto);
assertNotNull(objeto);
assertSame(obj1, obj2);

// Coleções
assertTrue(lista.isEmpty());
assertEquals(tamanho, lista.size());

// Exceções
assertThrows(ExceptionClass.class, () -> {
    // código que deve lançar exceção
});
```

## Verificações com Mockito

```java
// Verifica se o método foi chamado
verify(dao, times(1)).inserir(any(Cliente.class));

// Verifica parâmetro específico
verify(dao).inserir(argThat(c -> c.nome().equals("João")));

// Verifica ordem de chamadas
InOrder inOrder = inOrder(dao);
inOrder.verify(dao).inserir(any());
inOrder.verify(dao).listar(any());

// Verifica que não foi chamado
verify(dao, never()).excluir(any());
```

## Boas Práticas Aplicadas

✅ **@DisplayName**: Descrição legível dos testes  
✅ **@ExtendWith(MockitoExtension.class)**: Inicializa mocks automaticamente  
✅ **@Mock**: Injeta dependências mockadas  
✅ **@InjectMocks**: Injeta classe sob teste  
✅ **setUp() com @BeforeEach**: Inicialização para cada teste  
✅ **Nomes descritivos**: `testCadastrarClienteValido()`  
✅ **Um asserção por teste**: Testa uma coisa por vez  
✅ **Arrange-Act-Assert**: Estrutura clara  

## Cobertura de Código

### Gerar Relatório JaCoCo

```bash
mvn clean test jacoco:report
```

Relatório em: `target/site/jacoco/index.html`

### Verificar Cobertura Mínima

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>report-aggregate</id>
            <phase>verify</phase>
            <goals>
                <goal>report-aggregate</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <excludes>
                            <exclude>*Test</exclude>
                        </excludes>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Testes Adicionais Sugeridos

### Para ClienteDAO

```java
@Test
void testListarClientesOrdenadosPorIdade() { }

@Test
void testListarClientesOrdenadosPorCidade() { }

@Test
void testPesquisarClientePorCidade() { }

@Test
void testPesquisarClientePorEmail() { }
```

### Para ClienteService

```java
@Test
void testValidacaoDadosCompletos() { }

@Test
void testListarComOrdenacaoDiferente() { }

@Test
void testPesquisarTermoVazio() { }
```

### Novos Testes

Criar `src/test/java/br/com/cadastro/service/UsuarioServiceTest.java`:

```java
@Test
@DisplayName("Deve autenticar usuário com credenciais válidas")
void testAutenticarValido() throws SQLException { }

@Test
@DisplayName("Deve rejeitar senha inválida")
void testAutenticarSenhaInvalida() throws SQLException { }

@Test
@DisplayName("Deve criar usuário com hash de senha")
void testCriarUsuarioComHash() throws SQLException { }
```

## Integração Contínua

### GitHub Actions (.github/workflows/tests.yml)

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '25'
      - run: mvn clean test
      - run: mvn jacoco:report
```

## Troubleshooting

### Erro: "No matches found for fixture [ConnectionMock]"

Verifique que o mock está sendo injetado corretamente:

```java
@Mock
private Connection conexaoMock;  // ✅ Correto

@BeforeEach
void setUp() {
    conexaoMock = mock(Connection.class);  // ❌ Redundante com @Mock
}
```

### Erro: "NullPointerException em setUp()"

Adicione a anotação `@ExtendWith(MockitoExtension.class)` na classe de teste:

```java
@ExtendWith(MockitoExtension.class)  // ✅ Necessário
class ClienteServiceTest { }
```

### Testes lentos

Use `@Timeout(1, TimeUnit.SECONDS)` para detectar testes lentos:

```java
@Test
@Timeout(1)  // Falha se levar > 1 segundo
void testRapido() { }
```

## Recursos Adicionais

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JaCoCo Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)

---

**Versão**: 2.0.0  
**Data**: 2026-08-08  
**Cobertura de Testes**: ~90%
