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

  /**
   * Busca um usuário pelo login.
   *
   * @param login o login do usuário
   * @return Optional contendo o usuário se encontrado
   * @throws SQLException se houver erro na conexão
   */
  public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
    String sql = "SELECT id, login, senha, nome, ativo FROM usuarios WHERE login = ? AND ativo = 1";
    try (Connection conexao = Conexao.abrir();
        PreparedStatement comando = conexao.prepareStatement(sql)) {
      comando.setString(1, login);
      try (ResultSet resultado = comando.executeQuery()) {
        return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
      }
    }
  }

  public Optional<Usuario> buscarPorId(long id) throws SQLException {
    String sql = "SELECT id, login, senha, nome, ativo FROM usuarios WHERE id = ? AND ativo = 1";
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
    String sql = "BEGIN INSERT INTO usuarios (login, senha, nome, ativo) "
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
    String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
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
    String sql = "UPDATE usuarios SET ativo = 0 WHERE id = ?";
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
