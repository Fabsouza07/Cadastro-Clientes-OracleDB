package br.com.cadastro.dao;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import br.com.cadastro.model.Usuario;
import br.com.cadastro.util.Conexao;

/** DAO para gerenciar usuários no banco de dados. */
public class UsuarioDAO {
  private static final String USUARIOS = Conexao.tabela("usuarios");

  public record EstadoAutenticacao(Usuario usuario, int tentativasFalhas, boolean bloqueado) {}

  public Optional<EstadoAutenticacao> buscarParaAutenticacao(String login) throws SQLException {
    String sql =
        "SELECT id, login, senha, nome, ativo, NVL(tentativas_falhas, 0) tentativas_falhas, "
            + "CASE WHEN bloqueado_ate > SYSTIMESTAMP THEN 1 ELSE 0 END bloqueado "
            + "FROM " + USUARIOS + " WHERE login = ? AND ativo = 1";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setString(1, login);
      try (ResultSet resultado = comando.executeQuery()) {
        if (!resultado.next()) return Optional.empty();
        return Optional.of(
            new EstadoAutenticacao(
                mapear(resultado), resultado.getInt("tentativas_falhas"), resultado.getInt("bloqueado") == 1));
      }
    }
  }

  public void registrarFalha(long id) throws SQLException {
    String sql =
        "UPDATE " + USUARIOS + " SET "
            + "tentativas_falhas = CASE "
            + "WHEN bloqueado_ate IS NOT NULL AND bloqueado_ate <= SYSTIMESTAMP THEN 1 "
            + "WHEN NVL(tentativas_falhas, 0) >= 2 THEN 3 "
            + "ELSE NVL(tentativas_falhas, 0) + 1 END, "
            + "bloqueado_ate = CASE "
            + "WHEN bloqueado_ate IS NOT NULL AND bloqueado_ate <= SYSTIMESTAMP THEN NULL "
            + "WHEN NVL(tentativas_falhas, 0) >= 2 THEN SYSTIMESTAMP + INTERVAL '10' MINUTE "
            + "ELSE bloqueado_ate END "
            + "WHERE id = ?";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setLong(1, id);
      comando.executeUpdate();
    }
  }

  public void limparFalhas(long id) throws SQLException {
    String sql = "UPDATE " + USUARIOS + " SET tentativas_falhas = 0, bloqueado_ate = NULL WHERE id = ?";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setLong(1, id);
      comando.executeUpdate();
    }
  }

  /**
   * Busca um usuário pelo login.
   *
   * @param login o login do usuário
   * @return Optional contendo o usuário se encontrado
   * @throws SQLException se houver erro na conexão
   */
  public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
    String sql = "SELECT id, login, senha, nome, ativo FROM " + USUARIOS + " WHERE login = ? AND ativo = 1";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setString(1, login);
      try (ResultSet resultado = comando.executeQuery()) {
        return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
      }
    }
  }

  public Optional<Usuario> buscarPorId(long id) throws SQLException {
    String sql = "SELECT id, login, senha, nome, ativo FROM " + USUARIOS + " WHERE id = ? AND ativo = 1";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setLong(1, id);
      try (ResultSet resultado = comando.executeQuery()) {
        return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
      }
    }
  }

  /**
   * Cria um novo usuário no banco.
   *
   * @param usuario o usuário a ser inserido
   * @return o ID do usuário criado
   * @throws SQLException se houver erro na conexão
   */
  public long criar(Usuario usuario) throws SQLException {
    String sql = "BEGIN INSERT INTO " + USUARIOS + " (login, senha, nome, ativo) "
        + "VALUES (?, ?, ?, 1) RETURNING id INTO ?; END;";
    try (Connection conexao = Conexao.abrir();
        CallableStatement comando = conexao.prepareCall(sql)) {
      comando.setString(1, usuario.login());
      comando.setString(2, usuario.senha());
      comando.setString(3, usuario.nome());
      comando.registerOutParameter(4, java.sql.Types.BIGINT);
      comando.executeUpdate();
      return comando.getLong(4);
    }
  }

  /**
   * Atualiza a senha de um usuário.
   *
   * @param id o ID do usuário
   * @param novaSenha a nova senha (hash)
   * @return true se atualização foi bem-sucedida
   * @throws SQLException se houver erro na conexão
   */
  public boolean atualizarSenha(long id, String novaSenha) throws SQLException {
    String sql = "UPDATE " + USUARIOS + " SET senha = ?, tentativas_falhas = 0, bloqueado_ate = NULL WHERE id = ?";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setString(1, novaSenha);
      comando.setLong(2, id);
      return comando.executeUpdate() == 1;
    }
  }

  /**
   * Desativa um usuário (soft delete).
   *
   * @param id o ID do usuário
   * @return true se desativação foi bem-sucedida
   * @throws SQLException se houver erro na conexão
   */
  public boolean desativar(long id) throws SQLException {
    String sql = "UPDATE " + USUARIOS + " SET ativo = 0 WHERE id = ?";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setLong(1, id);
      return comando.executeUpdate() == 1;
    }
  }

  /**
   * Mapeia um ResultSet para um objeto Usuario.
   *
   * @param rs o ResultSet
   * @return o usuário mapeado
   * @throws SQLException se houver erro ao ler dados
   */
  private static Usuario mapear(ResultSet rs) throws SQLException {
    return new Usuario(
        rs.getLong("id"),
        rs.getString("login"),
        rs.getString("senha"),
        rs.getString("nome"),
        rs.getInt("ativo") == 1);
  }
}
