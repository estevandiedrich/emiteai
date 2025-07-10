package com.emiteai.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    @Test
    void testCreatePessoa() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setTelefone("(11) 99999-9999");
        pessoa.setCpf("123.456.789-00");

        // When & Then
        assertEquals("João Silva", pessoa.getNome());
        assertEquals("(11) 99999-9999", pessoa.getTelefone());
        assertEquals("123.456.789-00", pessoa.getCpf());
        assertNull(pessoa.getId());
        assertNull(pessoa.getEndereco());
    }

    @Test
    void testPessoaWithEndereco() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setCpf("987.654.321-00");

        Endereco endereco = new Endereco();
        endereco.setCep("01234-567");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");
        endereco.setPessoa(pessoa);

        pessoa.setEndereco(endereco);

        // When & Then
        assertNotNull(pessoa.getEndereco());
        assertEquals("01234-567", pessoa.getEndereco().getCep());
        assertEquals("Centro", pessoa.getEndereco().getBairro());
        assertEquals("São Paulo", pessoa.getEndereco().getMunicipio());
        assertEquals("SP", pessoa.getEndereco().getEstado());
        assertEquals(pessoa, pessoa.getEndereco().getPessoa());
    }

    @Test
    void testPessoaEquality() {
        // Given
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setId(1L);
        pessoa1.setNome("João");
        pessoa1.setCpf("123.456.789-00");

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setId(1L);
        pessoa2.setNome("João");
        pessoa2.setCpf("123.456.789-00");

        // When & Then
        assertNotEquals(pessoa1, pessoa2); // Default equals implementation
    }
}
