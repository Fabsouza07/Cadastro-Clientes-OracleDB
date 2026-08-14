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

    if (url.isBlank()) {
      throw new SQLException(
          "Configure DB_URL (ou db.url em config.properties local) antes de iniciar a aplicação.");
    }

    if (usuario == null || usuario.isBlank()) {
      return DriverManager.getConnection(url);
    }

    return DriverManager.getConnection(url, usuario, senha);
  }

  public static String tabela(String nomeTabela) {
    String schema = Config.get("db.schema");
    if (schema.isBlank()) schema = "cadastro_clientes";
    if (!schema.matches("[A-Za-z][A-Za-z0-9_$#]*") || !nomeTabela.matches("[A-Za-z][A-Za-z0-9_$#]*")) {
      throw new IllegalStateException("Nome de schema ou tabela inválido na configuração.");
    }
    return schema + "." + nomeTabela;
  }

  public static void testar() throws SQLException {
    try (Connection conexao = abrir()) {
      if (!conexao.isValid(5)) {
        throw new SQLException("A conexão foi aberta, mas não foi validada.");
      }
    }
  }
}
