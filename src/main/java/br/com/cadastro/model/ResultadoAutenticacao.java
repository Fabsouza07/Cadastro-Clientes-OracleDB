package br.com.cadastro.model;

import java.util.Optional;

/** Resultado de uma tentativa de login, incluindo o estado de bloqueio registrado no banco. */
public record ResultadoAutenticacao(
    Optional<Usuario> usuario, int tentativasRestantes, boolean bloqueado) {

  public static ResultadoAutenticacao sucesso(Usuario usuario) {
    return new ResultadoAutenticacao(Optional.of(usuario), 0, false);
  }

  public static ResultadoAutenticacao invalido(int tentativasRestantes) {
    return new ResultadoAutenticacao(Optional.empty(), tentativasRestantes, false);
  }

  public static ResultadoAutenticacao contaBloqueada() {
    return new ResultadoAutenticacao(Optional.empty(), 0, true);
  }
}
