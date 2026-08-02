package br.com.cadastro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexao {

  private Conexao() {}

  public static Connection abrir() throws SQLException {
    String url = Config.get("db.url");
    String usuario = Config.get("db.usuario");
    String senha = Config.get("db.senha");

    if (usuario == null || usuario.isBlank()) {
      return DriverManager.getConnection(url);
    }

    return DriverManager.getConnection(url, usuario, senha);
  }

  public static void testar() throws SQLException {
    try (Connection conexao = abrir()) {
      if (!conexao.isValid(5)) {
        throw new SQLException("A conexão foi aberta, mas não foi validada.");
      }
    }
  }
}
