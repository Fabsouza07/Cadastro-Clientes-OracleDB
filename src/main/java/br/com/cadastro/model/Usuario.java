package br.com.cadastro.model;

/** Modelo de usuário para autenticação. */
public record Usuario(
    Long id,
    String login,
    String senha,
    String nome,
    boolean ativo
) {}
