package com.emiteai.controller;

import com.emiteai.dtos.EnderecoDTO;
import com.emiteai.service.AuditoriaService;
import com.emiteai.service.ViaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CepController.class)
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ViaCepService viaCepService;

    @MockBean
    private AuditoriaService auditoriaService; // Mock do serviço de auditoria

    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setCep("01234-567");
        enderecoDTO.setBairro("Centro");
        enderecoDTO.setMunicipio("São Paulo");
        enderecoDTO.setEstado("SP");
    }

    @Test
    void testBuscarEndereco_Success() throws Exception {
        // Given
        String cep = "01234567";
        when(viaCepService.buscarEnderecoPorCep(cep)).thenReturn(enderecoDTO);

        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01234-567"))
                .andExpect(jsonPath("$.bairro").value("Centro"))
                .andExpect(jsonPath("$.municipio").value("São Paulo"))
                .andExpect(jsonPath("$.estado").value("SP"));

        verify(viaCepService).buscarEnderecoPorCep(cep);
    }

    @Test
    void testBuscarEndereco_WithFormattedCep() throws Exception {
        // Given
        String cep = "01234-567";
        when(viaCepService.buscarEnderecoPorCep(cep)).thenReturn(enderecoDTO);

        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01234-567"));

        verify(viaCepService).buscarEnderecoPorCep(cep);
    }

    @Test
    void testBuscarEndereco_CepNotFound() throws Exception {
        // Given
        String cep = "99999999";
        when(viaCepService.buscarEnderecoPorCep(cep)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(viaCepService).buscarEnderecoPorCep(cep);
    }

    @Test
    void testBuscarEndereco_ServiceException() throws Exception {
        // Given
        String cep = "00000000";
        when(viaCepService.buscarEnderecoPorCep(cep))
                .thenThrow(new RestClientException("Erro ao consultar CEP"));

        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", cep))
                .andExpect(status().isBadRequest());

        verify(viaCepService).buscarEnderecoPorCep(cep);
    }

    @Test
    void testBuscarEndereco_InvalidCep() throws Exception {
        // Given
        String invalidCep = "invalid";
        EnderecoDTO errorResponse = new EnderecoDTO();
        when(viaCepService.buscarEnderecoPorCep(invalidCep)).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", invalidCep))
                .andExpect(status().isOk());

        verify(viaCepService).buscarEnderecoPorCep(invalidCep);
    }

    @Test
    void testBuscarEndereco_EmptyCep() throws Exception {
        // Given
        String emptyCep = "";
        
        // When & Then
        mockMvc.perform(get("/api/cep/{cep}", emptyCep))
                .andExpect(status().isInternalServerError()); // 500 porque CEP vazio causa erro interno

        verify(viaCepService, never()).buscarEnderecoPorCep(anyString());
    }
}
