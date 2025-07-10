package com.emiteai.controller;

import com.emiteai.service.CsvProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelatorioController.class)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvProducer csvProducer;

    @BeforeEach
    void setUp() {
        // Limpar qualquer configuração anterior se necessário
    }

    @Test
    void testGerarCsv_DeveRetornarAccepted() throws Exception {
        // Given
        doNothing().when(csvProducer).enviarMensagem("Gerar CSV");

        // When & Then
        mockMvc.perform(post("/api/relatorios/csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Relatório CSV sendo gerado"));

        // Verify
        verify(csvProducer, times(1)).enviarMensagem("Gerar CSV");
    }

    @Test
    void testGerarCsv_ComExcecaoNoProducer() throws Exception {
        // Given
        doThrow(new RuntimeException("Erro ao enviar mensagem"))
                .when(csvProducer).enviarMensagem("Gerar CSV");

        // When & Then - GlobalExceptionHandler converts RuntimeException to 400 Bad Request
        mockMvc.perform(post("/api/relatorios/csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadCsv_ArquivoExiste_DeveRetornarArquivo() throws Exception {
        // Given - Criar arquivo temporário para teste
        File tempFile = new File("/tmp/pessoas.csv");
        createTestCsvFile(tempFile);

        try {
            // When & Then
            mockMvc.perform(get("/api/relatorios/download"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=pessoas.csv"))
                    .andExpect(content().contentType("text/csv"));
        } finally {
            // Cleanup
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Test
    void testDownloadCsv_ArquivoNaoExiste_DeveRetornarNotFound() throws Exception {
        // Given - Garantir que o arquivo não existe
        File file = new File("/tmp/pessoas.csv");
        if (file.exists()) {
            file.delete();
        }

        // When & Then
        mockMvc.perform(get("/api/relatorios/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDownloadCsv_ComConteudoValido() throws Exception {
        // Given - Criar arquivo com conteúdo específico
        File tempFile = new File("/tmp/pessoas.csv");
        String csvContent = "Nome,Telefone,CPF\nJoão Silva,(11) 99999-9999,123.456.789-00\n";
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(csvContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // When & Then
            mockMvc.perform(get("/api/relatorios/download"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=pessoas.csv"))
                    .andExpect(content().contentType("text/csv"));
        } finally {
            // Cleanup
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void createTestCsvFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Nome,Telefone,CPF\n");
            writer.write("João Silva,(11) 99999-9999,123.456.789-00\n");
            writer.write("Maria Santos,(11) 88888-8888,987.654.321-00\n");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar arquivo de teste", e);
        }
    }
}
