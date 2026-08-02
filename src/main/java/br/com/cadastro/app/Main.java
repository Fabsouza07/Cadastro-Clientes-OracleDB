package br.com.cadastro.app;

import br.com.cadastro.util.Conexao;
import java.awt.*;
import javax.swing.*;

/** Ponto de entrada da aplicação gráfica. */
public final class Main {
  private Main() {}

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          aplicarTema();
          SplashScreen splash = new SplashScreen();
          splash.setVisible(true);

          new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
              Conexao.testar();
              Thread.sleep(1100); // Mantém a apresentação perceptível ao usuário.
              return null;
            }

            @Override
            protected void done() {
              splash.dispose();
              try {
                get();
                new MainFrame().setVisible(true);
              } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Não foi possível conectar ao banco de dados.\n\n" + causa(e),
                    "Falha na conexão",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
              }
            }
          }.execute();
        });
  }

  private static void aplicarTema() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
      // Usa o tema padrão caso o tema do sistema não esteja disponível.
    }
    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI Semibold", Font.PLAIN, 13));
  }

  private static String causa(Exception e) {
    Throwable causa = e.getCause() == null ? e : e.getCause();
    return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
  }
}
