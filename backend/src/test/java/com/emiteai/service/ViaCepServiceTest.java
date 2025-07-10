package com.emiteai.service;

import com.emiteai.dtos.EnderecoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViaCepServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ViaCepService viaCepService;

    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        // Mock the RestTemplate in ViaCepService
        viaCepService = new ViaCepService() {
            private final RestTemplate mockRestTemplate = restTemplate;
            
            @Override
            public EnderecoDTO buscarEnderecoPorCep(String cep) {
                String url = "https://viacep.com.br/ws/" + cep + "/json/";
                return mockRestTemplate.getForObject(url, EnderecoDTO.class);
            }
        };

        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setCep("01234-567");
        enderecoDTO.setBairro("Centro");
        enderecoDTO.setMunicipio("São Paulo");
        enderecoDTO.setEstado("SP");
    }

    @Test
    void testBuscarEnderecoPorCep_Success() {
        // Given
        String cep = "01234567";
        String expectedUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        
        when(restTemplate.getForObject(expectedUrl, EnderecoDTO.class))
            .thenReturn(enderecoDTO);

        // When
        EnderecoDTO result = viaCepService.buscarEnderecoPorCep(cep);

        // Then
        assertNotNull(result);
        assertEquals("01234-567", result.getCep());
        assertEquals("Centro", result.getBairro());
        assertEquals("São Paulo", result.getMunicipio());
        assertEquals("SP", result.getEstado());
        
        verify(restTemplate).getForObject(expectedUrl, EnderecoDTO.class);
    }

    @Test
    void testBuscarEnderecoPorCep_WithFormattedCep() {
        // Given
        String cep = "01234-567";
        String expectedUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        
        when(restTemplate.getForObject(expectedUrl, EnderecoDTO.class))
            .thenReturn(enderecoDTO);

        // When
        EnderecoDTO result = viaCepService.buscarEnderecoPorCep(cep);

        // Then
        assertNotNull(result);
        assertEquals("01234-567", result.getCep());
        verify(restTemplate).getForObject(expectedUrl, EnderecoDTO.class);
    }

    @Test
    void testBuscarEnderecoPorCep_RestClientException() {
        // Given
        String cep = "00000000";
        String expectedUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        
        when(restTemplate.getForObject(expectedUrl, EnderecoDTO.class))
            .thenThrow(new RestClientException("Erro ao consultar CEP"));

        // When & Then
        assertThrows(RestClientException.class, () -> {
            viaCepService.buscarEnderecoPorCep(cep);
        });
        
        verify(restTemplate).getForObject(expectedUrl, EnderecoDTO.class);
    }

    @Test
    void testBuscarEnderecoPorCep_ReturnsNull() {
        // Given
        String cep = "99999999";
        String expectedUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        
        when(restTemplate.getForObject(expectedUrl, EnderecoDTO.class))
            .thenReturn(null);

        // When
        EnderecoDTO result = viaCepService.buscarEnderecoPorCep(cep);

        // Then
        assertNull(result);
        verify(restTemplate).getForObject(expectedUrl, EnderecoDTO.class);
    }

    @Test
    void testBuscarEnderecoPorCep_InvalidCep() {
        // Given
        String invalidCep = "invalid";
        String expectedUrl = "https://viacep.com.br/ws/" + invalidCep + "/json/";
        
        EnderecoDTO errorResponse = new EnderecoDTO();
        when(restTemplate.getForObject(expectedUrl, EnderecoDTO.class))
            .thenReturn(errorResponse);

        // When
        EnderecoDTO result = viaCepService.buscarEnderecoPorCep(invalidCep);

        // Then
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, EnderecoDTO.class);
    }
}
