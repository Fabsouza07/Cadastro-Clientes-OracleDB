package br.com.cadastro.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
  private static final Properties P = new Properties();

  static {
    try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
      if (in != null) {
        P.load(in);
      }
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private Config() {}

  public static String get(String chave) {
    String nomeVariavel =
        chave.startsWith("db.")
            ? "ORACLE_" + chave.toUpperCase().replace('.', '_')
            : chave.toUpperCase().replace('.', '_');
    String env = System.getenv(nomeVariavel);
    return env != null && !env.isBlank() ? env : P.getProperty(chave, "").trim();
  }
}
