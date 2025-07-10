package com.emiteai.dtos;

import com.emiteai.entities.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoDTOTest {

    @Test
    void testConstructorDefault() {
        // When
        EnderecoDTO dto = new EnderecoDTO();

        // Then
        assertNull(dto.getNumero());
        assertNull(dto.getComplemento());
        assertNull(dto.getCep());
        assertNull(dto.getBairro());
        assertNull(dto.getMunicipio());
        assertNull(dto.getEstado());
    }

    @Test
    void testConstructorFromEntity() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setNumero("123");
        endereco.setComplemento("Apto 45");
        endereco.setCep("01234-567");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");

        // When
        EnderecoDTO dto = new EnderecoDTO(endereco);

        // Then
        assertEquals("123", dto.getNumero());
        assertEquals("Apto 45", dto.getComplemento());
        assertEquals("01234-567", dto.getCep());
        assertEquals("Centro", dto.getBairro());
        assertEquals("São Paulo", dto.getMunicipio());
        assertEquals("SP", dto.getEstado());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        EnderecoDTO dto = new EnderecoDTO();

        // When
        dto.setNumero("456");
        dto.setComplemento("Casa");
        dto.setCep("12345-678");
        dto.setBairro("Vila Nova");
        dto.setMunicipio("Rio de Janeiro");
        dto.setEstado("RJ");

        // Then
        assertEquals("456", dto.getNumero());
        assertEquals("Casa", dto.getComplemento());
        assertEquals("12345-678", dto.getCep());
        assertEquals("Vila Nova", dto.getBairro());
        assertEquals("Rio de Janeiro", dto.getMunicipio());
        assertEquals("RJ", dto.getEstado());
    }

    @Test
    void testConstructorFromEntityWithNullValues() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setCep("01234-567");
        // outros campos ficam null

        // When
        EnderecoDTO dto = new EnderecoDTO(endereco);

        // Then
        assertNull(dto.getNumero());
        assertNull(dto.getComplemento());
        assertEquals("01234-567", dto.getCep());
        assertNull(dto.getBairro());
        assertNull(dto.getMunicipio());
        assertNull(dto.getEstado());
    }

    @Test
    void testConstructorFromEntityWithEmptyValues() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setNumero("");
        endereco.setComplemento("");
        endereco.setCep("");
        endereco.setBairro("");
        endereco.setMunicipio("");
        endereco.setEstado("");

        // When
        EnderecoDTO dto = new EnderecoDTO(endereco);

        // Then
        assertEquals("", dto.getNumero());
        assertEquals("", dto.getComplemento());
        assertEquals("", dto.getCep());
        assertEquals("", dto.getBairro());
        assertEquals("", dto.getMunicipio());
        assertEquals("", dto.getEstado());
    }

    @Test
    void testViaCepResponse() {
        // Given - Simular resposta do ViaCEP
        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep("01310-100");
        dto.setBairro("Bela Vista");
        dto.setMunicipio("São Paulo");
        dto.setEstado("SP");

        // When & Then
        assertEquals("01310-100", dto.getCep());
        assertEquals("Bela Vista", dto.getBairro());
        assertEquals("São Paulo", dto.getMunicipio());
        assertEquals("SP", dto.getEstado());
        assertNull(dto.getNumero());
        assertNull(dto.getComplemento());
    }
}
