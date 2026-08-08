package br.com.cadastro.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.cadastro.model.Cliente;
import br.com.cadastro.model.Estatisticas;
import br.com.cadastro.util.Conexao;

@DisplayName("ClienteDAO Tests")
@ExtendWith(MockitoExtension.class)
class ClienteDAOTest {

  private ClienteDAO dao;
  private Connection conexaoMock;

  @BeforeEach
  void setUp() {
    dao = new ClienteDAO();
    conexaoMock = mock(Connection.class);
  }

  @Test
  @DisplayName("Deve inserir cliente e retornar ID")
  void testInserirCliente() throws SQLException {
    Cliente cliente = new Cliente(
        null,
        "João Silva",
        35,
        "São Paulo",
        "joao@email.com",
        "(11) 3333-3333",
        "(11) 99999-9999"
    );

    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      CallableStatement stmtMock = mock(CallableStatement.class);
      when(conexaoMock.prepareCall(anyString())).thenReturn(stmtMock);
      when(stmtMock.getLong(7)).thenReturn(1L);

      long id = dao.inserir(cliente);

      assertEquals(1L, id);
      verify(conexaoMock, times(1)).prepareCall(anyString());
    }
  }

  @Test
  @DisplayName("Deve listar clientes ordenados por nome")
  void testListarClientesOrdenadosPorNome() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      ResultSet rsMock = mock(ResultSet.class);
      
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeQuery()).thenReturn(rsMock);
      when(rsMock.next()).thenReturn(true).thenReturn(false);
      when(rsMock.getLong("id")).thenReturn(1L);
      when(rsMock.getString("nome")).thenReturn("João Silva");
      when(rsMock.getInt("idade")).thenReturn(35);
      when(rsMock.getString("cidade")).thenReturn("São Paulo");
      when(rsMock.getString("email")).thenReturn("joao@email.com");
      when(rsMock.getString("telefone_fixo")).thenReturn("(11) 3333-3333");
      when(rsMock.getString("telefone_celular")).thenReturn("(11) 99999-9999");

      List<Cliente> clientes = dao.listar("nome");

      assertEquals(1, clientes.size());
      assertEquals("João Silva", clientes.get(0).nome());
      verify(conexaoMock, times(1)).prepareStatement(anyString());
    }
  }

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
      when(rsMock.getLong("id")).thenReturn(1L);
      when(rsMock.getString("nome")).thenReturn("João Silva");
      when(rsMock.getInt("idade")).thenReturn(35);
      when(rsMock.getString("cidade")).thenReturn("São Paulo");
      when(rsMock.getString("email")).thenReturn("joao@email.com");
      when(rsMock.getString("telefone_fixo")).thenReturn("(11) 3333-3333");
      when(rsMock.getString("telefone_celular")).thenReturn("(11) 99999-9999");

      Optional<Cliente> cliente = dao.buscarPorId(1L);

      assertTrue(cliente.isPresent());
      assertEquals("João Silva", cliente.get().nome());
      verify(stmtMock, times(1)).setLong(1, 1L);
    }
  }

  @Test
  @DisplayName("Deve retornar vazio quando cliente não existe")
  void testBuscarClienteNaoExistente() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      ResultSet rsMock = mock(ResultSet.class);
      
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeQuery()).thenReturn(rsMock);
      when(rsMock.next()).thenReturn(false);

      Optional<Cliente> cliente = dao.buscarPorId(999L);

      assertFalse(cliente.isPresent());
    }
  }

  @Test
  @DisplayName("Deve atualizar cliente")
  void testAtualizarCliente() throws SQLException {
    Cliente cliente = new Cliente(
        1L,
        "João Silva Atualizado",
        36,
        "Rio de Janeiro",
        "joao.novo@email.com",
        "(21) 2222-2222",
        "(21) 98888-8888"
    );

    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeUpdate()).thenReturn(1);

      boolean resultado = dao.atualizar(cliente);

      assertTrue(resultado);
      verify(stmtMock, times(1)).executeUpdate();
    }
  }

  @Test
  @DisplayName("Deve excluir cliente")
  void testExcluirCliente() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeUpdate()).thenReturn(1);

      boolean resultado = dao.excluir(1L);

      assertTrue(resultado);
      verify(stmtMock, times(1)).setLong(1, 1L);
      verify(stmtMock, times(1)).executeUpdate();
    }
  }

  @Test
  @DisplayName("Deve retornar false quando exclusão falha")
  void testExcluirClienteFalha() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeUpdate()).thenReturn(0);

      boolean resultado = dao.excluir(999L);

      assertFalse(resultado);
    }
  }

  @Test
  @DisplayName("Deve pesquisar cliente por nome")
  void testPesquisarClientePorNome() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      PreparedStatement stmtMock = mock(PreparedStatement.class);
      ResultSet rsMock = mock(ResultSet.class);
      
      when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
      when(stmtMock.executeQuery()).thenReturn(rsMock);
      when(rsMock.next()).thenReturn(true).thenReturn(false);
      when(rsMock.getLong("id")).thenReturn(1L);
      when(rsMock.getString("nome")).thenReturn("João Silva");
      when(rsMock.getInt("idade")).thenReturn(35);
      when(rsMock.getString("cidade")).thenReturn("São Paulo");
      when(rsMock.getString("email")).thenReturn("joao@email.com");
      when(rsMock.getString("telefone_fixo")).thenReturn("(11) 3333-3333");
      when(rsMock.getString("telefone_celular")).thenReturn("(11) 99999-9999");

      List<Cliente> clientes = dao.pesquisar("João");

      assertEquals(1, clientes.size());
      verify(stmtMock, times(1)).setString(1, "%João%");
    }
  }

  @Test
  @DisplayName("Deve obter estatísticas")
  void testObterEstatisticas() throws SQLException {
    try (MockedStatic<Conexao> mockedConexao = mockStatic(Conexao.class)) {
      mockedConexao.when(Conexao::abrir).thenReturn(conexaoMock);

      Statement stmtMock = mock(Statement.class);
      ResultSet rsTotal = mock(ResultSet.class);
      ResultSet rsMaisVelho = mock(ResultSet.class);
      ResultSet rsCidade = mock(ResultSet.class);
      
      when(conexaoMock.createStatement()).thenReturn(stmtMock);
      when(stmtMock.executeQuery(contains("COUNT(*)"))).thenReturn(rsTotal);
      when(stmtMock.executeQuery(contains("ORDER BY idade DESC"))).thenReturn(rsMaisVelho);
      when(stmtMock.executeQuery(contains("GROUP BY cidade"))).thenReturn(rsCidade);
      
      when(rsTotal.next()).thenReturn(true);
      when(rsTotal.getLong("total")).thenReturn(10L);
      when(rsTotal.getDouble("media")).thenReturn(35.5);
      
      when(rsMaisVelho.next()).thenReturn(true);
      when(rsMaisVelho.getString(1)).thenReturn("Pedro");
      
      when(rsCidade.next()).thenReturn(true);
      when(rsCidade.getString(1)).thenReturn("São Paulo");

      Estatisticas stats = dao.estatisticas();

      assertEquals(10L, stats.total());
      assertEquals(35.5, stats.idadeMedia());
      assertEquals("Pedro", stats.clienteMaisVelho());
      assertEquals("São Paulo", stats.cidadeMaisFrequente());
    }
  }
}
