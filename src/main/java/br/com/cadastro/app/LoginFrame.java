package br.com.cadastro.app;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import br.com.cadastro.model.Usuario;
import br.com.cadastro.model.ResultadoAutenticacao;
import br.com.cadastro.service.UsuarioService;

/** Tela de login para autenticação de usuários. */
final class LoginFrame extends JFrame {
  private static final Color NAVY = new Color(15, 23, 42), BLUE = new Color(37, 99, 235);
  private static final Color TEXT = new Color(30, 41, 59), MUTED = new Color(100, 116, 139);
  private final UsuarioService service = new UsuarioService();
  private final Consumer<Usuario> onLoginSuccess;

  LoginFrame(Consumer<Usuario> onLoginSuccess) {
    super("Cadastro de Clientes - Login");
    this.onLoginSuccess = onLoginSuccess;
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setUndecorated(true);
    setContentPane(createContent());
    setSize(450, 550);
    setLocationRelativeTo(null);
  }

  private JPanel createContent() {
    JPanel root = new JPanel(new GridBagLayout());
    root.setBackground(new Color(248, 250, 252));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(40, 0, 30, 0);

    // Logo
    JLabel logo = new JLabel("C", SwingConstants.CENTER);
    logo.setOpaque(true);
    logo.setBackground(BLUE);
    logo.setForeground(Color.WHITE);
    logo.setFont(new Font("Segoe UI", Font.BOLD, 48));
    logo.setPreferredSize(new Dimension(90, 90));
    root.add(logo, gbc);

    // Título
    gbc.gridy = 1;
    gbc.insets = new Insets(0, 0, 10, 0);
    JLabel title = new JLabel("Cadastro de Clientes");
    title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
    title.setForeground(TEXT);
    title.setHorizontalAlignment(SwingConstants.CENTER);
    root.add(title, gbc);

    // Subtítulo
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 0, 40, 0);
    JLabel subtitle = new JLabel("Faça login para continuar");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitle.setForeground(MUTED);
    subtitle.setHorizontalAlignment(SwingConstants.CENTER);
    root.add(subtitle, gbc);

    // Painel de formulário
    JPanel formPanel = createFormPanel();
    gbc.gridy = 3;
    gbc.insets = new Insets(0, 20, 0, 20);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    root.add(formPanel, gbc);

    return root;
  }

  private JPanel createFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 15, 0);

    // Campo de Login
    JLabel loginLabel = new JLabel("Login");
    loginLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    loginLabel.setForeground(TEXT);
    panel.add(loginLabel, gbc);

    gbc.gridy = 1;
    gbc.insets = new Insets(5, 0, 20, 0);
    JTextField loginField = new JTextField();
    loginField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginField.setPreferredSize(new Dimension(0, 36));
    loginField.setBorder(
        new CompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(8, 12, 8, 12)));
    panel.add(loginField, gbc);

    // Campo de Senha
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 0, 15, 0);
    JLabel senhaLabel = new JLabel("Senha");
    senhaLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    senhaLabel.setForeground(TEXT);
    panel.add(senhaLabel, gbc);

    gbc.gridy = 3;
    gbc.insets = new Insets(5, 0, 25, 0);
    JPasswordField senhaField = new JPasswordField();
    senhaField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    senhaField.setPreferredSize(new Dimension(0, 36));
    senhaField.setBorder(
        new CompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(8, 12, 8, 12)));
    panel.add(senhaField, gbc);

    // Label de erro
    JLabel errorLabel = new JLabel();
    errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    errorLabel.setForeground(new Color(239, 68, 68));
    gbc.gridy = 4;
    gbc.insets = new Insets(0, 0, 15, 0);
    panel.add(errorLabel, gbc);

    // Botão de Login
    gbc.gridy = 5;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JButton loginButton = new JButton("Entrar");
    loginButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    loginButton.setBackground(BLUE);
    loginButton.setForeground(Color.WHITE);
    loginButton.setBorderPainted(false);
    loginButton.setFocusPainted(false);
    loginButton.setPreferredSize(new Dimension(0, 40));
    loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loginButton.addActionListener(
        e -> {
          errorLabel.setText("");
          realizarLogin(
              loginField.getText().trim(),
              new String(senhaField.getPassword()),
              errorLabel,
              loginButton);
        });
    panel.add(loginButton, gbc);

    // Allow login with Enter key
    senhaField.addActionListener(e -> loginButton.doClick());

    return panel;
  }

  private void realizarLogin(String login, String senha, JLabel errorLabel, JButton loginButton) {
    if (login.isEmpty() || senha.isEmpty()) {
      errorLabel.setText("Login e senha são obrigatórios.");
      return;
    }
    loginButton.setEnabled(false);

    new SwingWorker<ResultadoAutenticacao, Void>() {
      @Override
      protected ResultadoAutenticacao doInBackground() throws Exception {
        return service.autenticar(login, senha);
      }

      @Override
      protected void done() {
        try {
          ResultadoAutenticacao resultado = get();
          if (resultado.usuario().isPresent()) {
            LoginFrame.this.dispose();
            onLoginSuccess.accept(resultado.usuario().get());
          } else {
            exibirFalhaAutenticacao(resultado, errorLabel);
            loginButton.setEnabled(true);
          }
        } catch (Exception e) {
          errorLabel.setText("Erro ao conectar ao banco: " + e.getMessage());
          loginButton.setEnabled(true);
        }
      }
    }.execute();
  }

  private static void exibirFalhaAutenticacao(ResultadoAutenticacao resultado, JLabel errorLabel) {
    if (resultado.bloqueado()) {
      errorLabel.setText("Conta bloqueada pelo banco. Aguarde 10 minutos para tentar novamente.");
    } else if (resultado.tentativasRestantes() > 0) {
      errorLabel.setText(
          "Login ou senha inválidos. Restam "
              + resultado.tentativasRestantes()
              + " tentativa"
              + (resultado.tentativasRestantes() == 1 ? "." : "s."));
    } else {
      errorLabel.setText("Login ou senha inválidos.");
    }
  }
}
