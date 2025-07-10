package com.emiteai.dtos;

import com.emiteai.entities.Pessoa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PessoaDTOTest {

    @Test
    void testConstructorDefault() {
        // When
        PessoaDTO dto = new PessoaDTO();

        // Then
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getTelefone());
        assertNull(dto.getCpf());
    }

    @Test
    void testConstructorFromEntity() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João Silva");
        pessoa.setTelefone("(11) 99999-9999");
        pessoa.setCpf("123.456.789-00");

        // When
        PessoaDTO dto = new PessoaDTO(pessoa);

        // Then
        assertEquals(1L, dto.getId());
        assertEquals("João Silva", dto.getNome());
        assertEquals("(11) 99999-9999", dto.getTelefone());
        assertEquals("123.456.789-00", dto.getCpf());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        PessoaDTO dto = new PessoaDTO();

        // When
        dto.setId(2L);
        dto.setNome("Maria Santos");
        dto.setTelefone("(11) 88888-8888");
        dto.setCpf("987.654.321-00");

        // Then
        assertEquals(2L, dto.getId());
        assertEquals("Maria Santos", dto.getNome());
        assertEquals("(11) 88888-8888", dto.getTelefone());
        assertEquals("987.654.321-00", dto.getCpf());
    }

    @Test
    void testConstructorFromEntityWithNullValues() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João");
        // telefone e cpf ficam null

        // When
        PessoaDTO dto = new PessoaDTO(pessoa);

        // Then
        assertEquals(1L, dto.getId());
        assertEquals("João", dto.getNome());
        assertNull(dto.getTelefone());
        assertNull(dto.getCpf());
    }

    @Test
    void testConstructorFromEntityWithEmptyValues() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("");
        pessoa.setTelefone("");
        pessoa.setCpf("");

        // When
        PessoaDTO dto = new PessoaDTO(pessoa);

        // Then
        assertEquals(1L, dto.getId());
        assertEquals("", dto.getNome());
        assertEquals("", dto.getTelefone());
        assertEquals("", dto.getCpf());
    }
}
