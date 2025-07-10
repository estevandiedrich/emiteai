package com.emiteai.controller;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.service.AuditoriaService;
import com.emiteai.service.PessoaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PessoaService pessoaService;

    @MockBean
    private AuditoriaService auditoriaService; // Mock do serviço de auditoria

    @Autowired
    private ObjectMapper objectMapper;

    private PessoaDTO pessoaDTO;

    @BeforeEach
    void setUp() {
        pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(1L);
        pessoaDTO.setNome("João Silva");
        pessoaDTO.setTelefone("(11) 99999-9999");
        pessoaDTO.setCpf("123.456.789-00");
    }

    @Test
    void testCadastrar_Success() throws Exception {
        // Given
        when(pessoaService.cadastrarPessoa(any(PessoaDTO.class))).thenReturn(pessoaDTO);

        // When & Then
        mockMvc.perform(post("/api/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"))
                .andExpect(jsonPath("$.telefone").value("(11) 99999-9999"));

        verify(pessoaService).cadastrarPessoa(any(PessoaDTO.class));
    }

    @Test
    void testCadastrar_BadRequest() throws Exception {
        // Given
        when(pessoaService.cadastrarPessoa(any(PessoaDTO.class)))
                .thenThrow(new RuntimeException("CPF já cadastrado"));

        // When & Then
        mockMvc.perform(post("/api/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isBadRequest());

        verify(pessoaService).cadastrarPessoa(any(PessoaDTO.class));
    }

    @Test
    void testListarTodas_Success() throws Exception {
        // Given
        PessoaDTO pessoa1 = new PessoaDTO();
        pessoa1.setId(1L);
        pessoa1.setNome("João");
        pessoa1.setCpf("111.111.111-11");

        PessoaDTO pessoa2 = new PessoaDTO();
        pessoa2.setId(2L);
        pessoa2.setNome("Maria");
        pessoa2.setCpf("222.222.222-22");

        List<PessoaDTO> pessoas = Arrays.asList(pessoa1, pessoa2);
        when(pessoaService.listarTodas()).thenReturn(pessoas);

        // When & Then
        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].nome").value("Maria"));

        verify(pessoaService).listarTodas();
    }

    @Test
    void testBuscarPorCpf_Success() throws Exception {
        // Given
        String cpf = "123.456.789-00";
        when(pessoaService.buscarPorCpf(cpf)).thenReturn(pessoaDTO);

        // When & Then
        mockMvc.perform(get("/api/pessoas/cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value(cpf));

        verify(pessoaService).buscarPorCpf(cpf);
    }

    @Test
    void testBuscarPorCpf_NotFound() throws Exception {
        // Given
        String cpf = "999.999.999-99";
        when(pessoaService.buscarPorCpf(cpf))
                .thenThrow(new RuntimeException("Pessoa não encontrada"));

        // When & Then
        mockMvc.perform(get("/api/pessoas/cpf/{cpf}", cpf))
                .andExpect(status().isBadRequest());

        verify(pessoaService).buscarPorCpf(cpf);
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        // Given
        Long id = 1L;
        when(pessoaService.buscarPorId(id)).thenReturn(pessoaDTO);

        // When & Then
        mockMvc.perform(get("/api/pessoas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(pessoaService).buscarPorId(id);
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        // Given
        Long id = 999L;
        when(pessoaService.buscarPorId(id))
                .thenThrow(new RuntimeException("Pessoa não encontrada"));

        // When & Then
        mockMvc.perform(get("/api/pessoas/{id}", id))
                .andExpect(status().isBadRequest());

        verify(pessoaService).buscarPorId(id);
    }

    @Test
    void testAtualizar_Success() throws Exception {
        // Given
        Long id = 1L;
        PessoaDTO updatedPessoa = new PessoaDTO();
        updatedPessoa.setId(id);
        updatedPessoa.setNome("João Silva Atualizado");
        updatedPessoa.setTelefone("(11) 88888-8888");
        updatedPessoa.setCpf("123.456.789-00");

        when(pessoaService.atualizarPessoa(eq(id), any(PessoaDTO.class)))
                .thenReturn(updatedPessoa);

        // When & Then
        mockMvc.perform(put("/api/pessoas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPessoa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.telefone").value("(11) 88888-8888"));

        verify(pessoaService).atualizarPessoa(eq(id), any(PessoaDTO.class));
    }

    @Test
    void testAtualizar_NotFound() throws Exception {
        // Given
        Long id = 999L;
        when(pessoaService.atualizarPessoa(eq(id), any(PessoaDTO.class)))
                .thenThrow(new RuntimeException("Pessoa não encontrada"));

        // When & Then
        mockMvc.perform(put("/api/pessoas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isBadRequest());

        verify(pessoaService).atualizarPessoa(eq(id), any(PessoaDTO.class));
    }

    @Test
    void testDeletar_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(pessoaService).deletar(id);

        // When & Then
        mockMvc.perform(delete("/api/pessoas/{id}", id))
                .andExpect(status().isOk());

        verify(pessoaService).deletar(id);
    }
}
