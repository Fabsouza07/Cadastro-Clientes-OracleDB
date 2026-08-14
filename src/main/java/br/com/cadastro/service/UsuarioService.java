package br.com.cadastro.service;

import java.sql.SQLException;
import java.util.Optional;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.cadastro.dao.UsuarioDAO;
import br.com.cadastro.model.ResultadoAutenticacao;
import br.com.cadastro.model.Usuario;

/** Serviço para autenticação de usuários. */
public final class UsuarioService {
  private final UsuarioDAO dao = new UsuarioDAO();

  /**
   * Autentica um usuário com login e senha.
   *
   * @param login o login do usuário
   * @param senha a senha em texto plano
   * @return resultado contendo o usuário autenticado ou o estado de bloqueio armazenado no banco
   * @throws SQLException se houver erro na conexão
   */
  public ResultadoAutenticacao autenticar(String login, String senha) throws SQLException {
    if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
      throw new IllegalArgumentException("Login e senha são obrigatórios.");
    }

    Optional<UsuarioDAO.EstadoAutenticacao> estado = dao.buscarParaAutenticacao(login);

    if (estado.isEmpty()) {
      return ResultadoAutenticacao.invalido(0);
    }
    if (estado.get().bloqueado()) return ResultadoAutenticacao.contaBloqueada();

    Usuario usuarioEncontrado = estado.get().usuario();
    // Verifica a senha usando bcrypt
    boolean senhaValida = BCrypt.verifyer()
        .verify(senha.toCharArray(), usuarioEncontrado.senha().toCharArray())
        .verified;

    if (senhaValida) {
      dao.limparFalhas(usuarioEncontrado.id());
      return ResultadoAutenticacao.sucesso(usuarioEncontrado);
    }

    dao.registrarFalha(usuarioEncontrado.id());
    UsuarioDAO.EstadoAutenticacao estadoAtualizado =
        dao.buscarParaAutenticacao(login).orElseThrow();
    return estadoAtualizado.bloqueado()
        ? ResultadoAutenticacao.contaBloqueada()
        : ResultadoAutenticacao.invalido(Math.max(0, 3 - estadoAtualizado.tentativasFalhas()));
  }

  /**
   * Cria um novo usuário com senha hash.
   *
   * @param login o login do usuário
   * @param senha a senha em texto plano
   * @param nome o nome do usuário
   * @return o ID do usuário criado
   * @throws SQLException se houver erro na conexão
   */
  public long criar(String login, String senha, String nome) throws SQLException {
    validar(login, senha, nome);

    // Hash da senha usando bcrypt com cost 12
    String senhaHash = BCrypt.withDefaults().hashToString(12, senha.toCharArray());

    Usuario usuario = new Usuario(null, login, senhaHash, nome, true);
    return dao.criar(usuario);
  }

  /**
   * Busca um usuário pelo login.
   *
   * @param login o login do usuário
   * @return Optional contendo o usuário se encontrado
   * @throws SQLException se houver erro na conexão
   */
  public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
    return dao.buscarPorLogin(login);
  }

  /**
   * Atualiza a senha de um usuário.
   *
   * @param id o ID do usuário
   * @param senhaAtual a senha atual em texto plano
   * @param novaSenha a nova senha em texto plano
   * @return true se atualização foi bem-sucedida
   * @throws SQLException se houver erro na conexão
   */
  public boolean atualizarSenha(long id, String senhaAtual, String novaSenha)
      throws SQLException {
    validarSenha(novaSenha);
    Optional<Usuario> usuario = dao.buscarPorId(id);

    if (usuario.isEmpty()) {
      return false;
    }

    // Verifica a senha atual
    boolean senhaValida =
        BCrypt.verifyer()
            .verify(senhaAtual.toCharArray(), usuario.get().senha().toCharArray())
            .verified;

    if (!senhaValida) {
      throw new IllegalArgumentException("Senha atual incorreta.");
    }

    // Hash da nova senha
    String novoHash = BCrypt.withDefaults().hashToString(12, novaSenha.toCharArray());
    return dao.atualizarSenha(id, novoHash);
  }

  /**
   * Desativa um usuário.
   *
   * @param id o ID do usuário
   * @return true se desativação foi bem-sucedida
   * @throws SQLException se houver erro na conexão
   */
  public boolean desativar(long id) throws SQLException {
    return dao.desativar(id);
  }

  private static void validar(String login, String senha, String nome) {
    if (login == null || login.isBlank()) {
      throw new IllegalArgumentException("Login obrigatório.");
    }
    validarSenha(senha);
    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("Nome obrigatório.");
    }
  }

  private static void validarSenha(String senha) {
    if (senha == null || senha.isBlank() || senha.length() < 6) {
      throw new IllegalArgumentException("Senha obrigatória e deve ter no mínimo 6 caracteres.");
    }
  }
}
