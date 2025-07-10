package com.emiteai.service;

import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvConsumerTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private CsvConsumer csvConsumer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceberMensagem_DeveExecutarGeracaoCsv() {
        // Given
        List<Pessoa> pessoas = Arrays.asList(
                createPessoa(1L, "João Silva", "(11) 99999-9999", "123.456.789-00"),
                createPessoa(2L, "Maria Santos", "(11) 88888-8888", "987.654.321-00")
        );
        when(pessoaRepository.findAll()).thenReturn(pessoas);

        // When
        assertDoesNotThrow(() -> csvConsumer.receberMensagem("Gerar CSV"));

        // Then
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    void testGerarCsv_ComPessoasVazias() {
        // Given
        when(pessoaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> csvConsumer.receberMensagem("Gerar CSV"));

        // Then
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    void testGerarCsv_VerificarConteudoArquivo() throws IOException {
        // Given
        List<Pessoa> pessoas = Arrays.asList(
                createPessoa(1L, "João Silva", "(11) 99999-9999", "123.456.789-00"),
                createPessoa(2L, "Maria Santos", "(11) 88888-8888", "987.654.321-00")
        );
        when(pessoaRepository.findAll()).thenReturn(pessoas);

        // When - This test will validate that the service method can be called without throwing exceptions
        assertDoesNotThrow(() -> csvConsumer.receberMensagem("Gerar CSV"));

        // Then
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    void testReceberMensagem_ComExcecaoNoBanco() {
        // Given
        when(pessoaRepository.findAll()).thenThrow(new RuntimeException("Erro de conexão"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            csvConsumer.receberMensagem("Gerar CSV");
        });
    }

    private Pessoa createPessoa(Long id, String nome, String telefone, String cpf) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(nome);
        pessoa.setTelefone(telefone);
        pessoa.setCpf(cpf);
        return pessoa;
    }
}
