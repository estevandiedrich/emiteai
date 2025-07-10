package com.emiteai.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest {

    @Test
    void testCreateEndereco() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setNumero("123");
        endereco.setComplemento("Apto 45");
        endereco.setCep("01234-567");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");

        // When & Then
        assertEquals("123", endereco.getNumero());
        assertEquals("Apto 45", endereco.getComplemento());
        assertEquals("01234-567", endereco.getCep());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("São Paulo", endereco.getMunicipio());
        assertEquals("SP", endereco.getEstado());
        assertNull(endereco.getId());
        assertNull(endereco.getPessoa());
    }

    @Test
    void testEnderecoWithPessoa() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf("123.456.789-00");

        Endereco endereco = new Endereco();
        endereco.setCep("01234-567");
        endereco.setBairro("Vila Madalena");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");
        endereco.setPessoa(pessoa);

        // When & Then
        assertNotNull(endereco.getPessoa());
        assertEquals("João Silva", endereco.getPessoa().getNome());
        assertEquals("123.456.789-00", endereco.getPessoa().getCpf());
    }

    @Test
    void testEnderecoMinimalData() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setCep("12345-678");

        // When & Then
        assertEquals("12345-678", endereco.getCep());
        assertNull(endereco.getNumero());
        assertNull(endereco.getComplemento());
        assertNull(endereco.getBairro());
        assertNull(endereco.getMunicipio());
        assertNull(endereco.getEstado());
    }
}
