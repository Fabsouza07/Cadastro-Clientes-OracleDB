package br.com.cadastro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.cadastro.model.Cliente;

@DisplayName("ClienteService Validation Tests")
class ClienteServiceTest {

  private final ClienteService service = new ClienteService();

  @Test
  @DisplayName("Deve rejeitar nome vazio")
  void testValidacaoNomeVazio() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("", 35, "São Paulo", "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar nome nulo")
  void testValidacaoNomeNulo() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar(null, 35, "São Paulo", "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar idade menor que 1")
  void testValidacaoIdadeMenorQue1() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 0, "São Paulo", "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar idade maior que 120")
  void testValidacaoIdadeMaiorQue120() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 121, "São Paulo", "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar cidade vazia")
  void testValidacaoCidadeVazia() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, "", "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar cidade nula")
  void testValidacaoCidadeNula() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, null, "joao@email.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar e-mail sem @")
  void testValidacaoEmailSemArroba() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, "São Paulo", "emailinvalido.com", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar e-mail sem domínio")
  void testValidacaoEmailSemDominio() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, "São Paulo", "email@", "(11) 3333-3333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar telefone fixo com menos de 10 dígitos")
  void testValidacaoTelefoneFixoIncompleto() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, "São Paulo", "joao@email.com", "(11) 333", "(11) 99999-9999")
    );
  }

  @Test
  @DisplayName("Deve rejeitar telefone celular com menos de 11 dígitos")
  void testValidacaoTelefoneCelularIncompleto() {
    assertThrows(IllegalArgumentException.class, () ->
        service.cadastrar("João", 35, "São Paulo", "joao@email.com", "(11) 3333-3333", "(11) 9999")
    );
  }

  @Test
  @DisplayName("Deve criar Cliente record com dados válidos")
  void testClienteRecordValido() {
    Cliente cliente = new Cliente(
        1L,
        "João Silva",
        35,
        "São Paulo",
        "joao@email.com",
        "(11) 3333-3333",
        "(11) 99999-9999"
    );

    assertEquals(1L, cliente.id());
    assertEquals("João Silva", cliente.nome());
    assertEquals(35, cliente.idade());
    assertEquals("São Paulo", cliente.cidade());
    assertEquals("joao@email.com", cliente.email());
    assertEquals("(11) 3333-3333", cliente.telefoneFixo());
    assertEquals("(11) 99999-9999", cliente.telefoneCelular());
  }
}
