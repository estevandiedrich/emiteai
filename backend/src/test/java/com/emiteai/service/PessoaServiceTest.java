package com.emiteai.service;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private PessoaService pessoaService;

    private PessoaDTO pessoaDTO;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("João Silva");
        pessoaDTO.setTelefone("(11) 99999-9999");
        pessoaDTO.setCpf("123.456.789-00");

        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João Silva");
        pessoa.setTelefone("(11) 99999-9999");
        pessoa.setCpf("123.456.789-00");
    }

    @Test
    void testCadastrarPessoa_Success() {
        // Given
        when(pessoaRepository.existsByCpf(anyString())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // When
        PessoaDTO result = pessoaService.cadastrarPessoa(pessoaDTO);

        // Then
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("123.456.789-00", result.getCpf());
        verify(pessoaRepository).existsByCpf("123.456.789-00");
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void testCadastrarPessoa_CpfAlreadyExists() {
        // Given
        when(pessoaRepository.existsByCpf(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> pessoaService.cadastrarPessoa(pessoaDTO));
        
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(pessoaRepository).existsByCpf("123.456.789-00");
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void testListarTodas() {
        // Given
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setId(1L);
        pessoa1.setNome("João");
        pessoa1.setCpf("111.111.111-11");

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setId(2L);
        pessoa2.setNome("Maria");
        pessoa2.setCpf("222.222.222-22");

        List<Pessoa> pessoas = Arrays.asList(pessoa1, pessoa2);
        when(pessoaRepository.findAll()).thenReturn(pessoas);

        // When
        List<PessoaDTO> result = pessoaService.listarTodas();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("João", result.get(0).getNome());
        assertEquals("Maria", result.get(1).getNome());
        verify(pessoaRepository).findAll();
    }

    @Test
    void testBuscarPorCpf_Success() {
        // Given
        when(pessoaRepository.findByCpf(anyString())).thenReturn(Optional.of(pessoa));

        // When
        PessoaDTO result = pessoaService.buscarPorCpf("123.456.789-00");

        // Then
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("123.456.789-00", result.getCpf());
        verify(pessoaRepository).findByCpf("123.456.789-00");
    }

    @Test
    void testBuscarPorCpf_NotFound() {
        // Given
        when(pessoaRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pessoaService.buscarPorCpf("999.999.999-99"));
        
        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(pessoaRepository).findByCpf("999.999.999-99");
    }

    @Test
    void testBuscarPorId_Success() {
        // Given
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        // When
        PessoaDTO result = pessoaService.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals(1L, result.getId());
        verify(pessoaRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NotFound() {
        // Given
        when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pessoaService.buscarPorId(999L));
        
        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(pessoaRepository).findById(999L);
    }

    @Test
    void testAtualizarPessoa_Success() {
        // Given
        PessoaDTO updateDTO = new PessoaDTO();
        updateDTO.setNome("João Silva Atualizado");
        updateDTO.setTelefone("(11) 88888-8888");
        updateDTO.setCpf("123.456.789-00");

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // When
        PessoaDTO result = pessoaService.atualizarPessoa(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(pessoaRepository).findById(1L);
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void testAtualizarPessoa_NotFound() {
        // Given
        PessoaDTO updateDTO = new PessoaDTO();
        updateDTO.setNome("João Silva Atualizado");

        when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pessoaService.atualizarPessoa(999L, updateDTO));
        
        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(pessoaRepository).findById(999L);
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void testDeletar() {
        // When
        pessoaService.deletar(1L);

        // Then
        verify(pessoaRepository).deleteById(1L);
    }
}
