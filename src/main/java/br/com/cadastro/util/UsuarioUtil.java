package br.com.cadastro.util;

import java.util.Scanner;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.cadastro.service.UsuarioService;

/**
 * Utilitário para gerenciar usuários via linha de comando. Execute este programa para criar,
 * listar ou gerenciar usuários do sistema.
 */
public final class UsuarioUtil {
  private static final Scanner scanner = new Scanner(System.in);
  private static final UsuarioService service = new UsuarioService();

  public static void main(String[] args) {
    Console.cabecalho("Oracle Database");
    exibirMenu();
  }

  private static void exibirMenu() {
    while (true) {
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║     GERENCIADOR DE USUÁRIOS           ║");
      System.out.println("╚════════════════════════════════════════╝");
      System.out.println("\n1. Criar novo usuário");
      System.out.println("2. Testar autenticação");
      System.out.println("3. Gerar hash de senha (bcrypt)");
      System.out.println("4. Sair");
      System.out.print("\nEscolha uma opção: ");

      String opcao = scanner.nextLine().trim();
      switch (opcao) {
        case "1" -> criarUsuario();
        case "2" -> testarAutenticacao();
        case "3" -> gerarHashSenha();
        case "4" -> {
          System.out.println("\nAté logo!");
          return;
        }
        default -> System.out.println("[ERRO] Opção inválida.");
      }
    }
  }

  private static void criarUsuario() {
    System.out.println("\n--- Criar Novo Usuário ---");
    String login = Console.ler("Login: ");
    String nome = Console.ler("Nome completo: ");
    String senha = Console.ler("Senha: ");
    String confirmacao = Console.ler("Confirme a senha: ");

    if (!senha.equals(confirmacao)) {
      Console.erro("As senhas não coincidem.");
      return;
    }

    try {
      long id = service.criar(login, senha, nome);
      Console.sucesso("Usuário criado com ID: " + id);
    } catch (IllegalArgumentException e) {
      Console.erro(e.getMessage());
    } catch (Exception e) {
      Console.erro("Erro ao criar usuário: " + e.getMessage());
    }
  }

  private static void testarAutenticacao() {
    System.out.println("\n--- Testar Autenticação ---");
    String login = Console.ler("Login: ");
    String senha = Console.ler("Senha: ");

    try {
      var resultado = service.autenticar(login, senha);
      if (resultado.usuario().isPresent()) {
        Console.sucesso("Autenticação bem-sucedida!");
        System.out.println("Usuário: " + resultado.usuario().get().nome());
      } else if (resultado.bloqueado()) {
        Console.aviso("Conta bloqueada. Aguarde 10 minutos para tentar novamente.");
      } else {
        Console.aviso("Login ou senha inválidos.");
      }
    } catch (Exception e) {
      Console.erro("Erro na autenticação: " + e.getMessage());
    }
  }

  private static void gerarHashSenha() {
    System.out.println("\n--- Gerar Hash de Senha ---");
    String senha = Console.ler("Digite a senha: ");
    String hash = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
    System.out.println("\nHash bcrypt (cost 12):");
    System.out.println(hash);
    System.out.println("\nUse este hash para inserir diretamente no banco de dados.");
  }
}
