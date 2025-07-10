package com.emiteai.service;

import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Temporariamente desabilitado até resolver dependências do Envers")
class EnversAuditServiceTest {

    @Autowired
    private EnversAuditService enversAuditService;

    @Autowired
    private PessoaRepository pessoaRepository;

    private Pessoa pessoaTeste;

    @BeforeEach
    void setUp() {
        // Criar uma pessoa para testar
        pessoaTeste = new Pessoa();
        pessoaTeste.setNome("João Silva");
        pessoaTeste.setCpf("12345678901");
        pessoaTeste.setTelefone("11999999999");
        pessoaTeste = pessoaRepository.save(pessoaTeste);
    }

    @Test
    void testFindRevisionsByPessoa_DeveRetornarRevisoes() {
        // Given
        // Modificar a pessoa para criar uma nova revisão
        pessoaTeste.setNome("João Silva Santos");
        pessoaRepository.save(pessoaTeste);

        // When
        List<Number> revisoes = enversAuditService.findRevisionsByPessoa(pessoaTeste.getId());

        // Then
        assertNotNull(revisoes);
        assertFalse(revisoes.isEmpty());
        // Deve ter pelo menos uma revisão (criação)
        assertTrue(revisoes.size() >= 1);
    }

    @Test
    void testCountPessoaRevisions_DeveContarRevisoesCorretamente() {
        // Given
        Long pessoaId = pessoaTeste.getId();
        
        // Fazer algumas modificações para gerar revisões
        pessoaTeste.setNome("Nome Modificado 1");
        pessoaRepository.save(pessoaTeste);
        
        pessoaTeste.setTelefone("11888888888");
        pessoaRepository.save(pessoaTeste);

        // When
        Long totalRevisoes = enversAuditService.countPessoaRevisions(pessoaId);

        // Then
        assertNotNull(totalRevisoes);
        // Deve ter pelo menos 1 revisão (criação) + modificações
        assertTrue(totalRevisoes >= 1);
    }

    @Test
    void testFindLastModificationDate_DeveRetornarDataValida() {
        // Given
        Long pessoaId = pessoaTeste.getId();

        // When
        Date ultimaModificacao = enversAuditService.findLastModificationDate(pessoaId);

        // Then
        assertNotNull(ultimaModificacao);
        // A data deve ser recente (últimos 5 minutos)
        long agora = System.currentTimeMillis();
        long diferencaMs = agora - ultimaModificacao.getTime();
        assertTrue(diferencaMs < 5 * 60 * 1000, "Data deve ser recente");
    }

    @Test
    void testFindPessoaAtRevision_DeveRetornarEstadoCorreto() {
        // Given
        String nomeOriginal = pessoaTeste.getNome();
        Long pessoaId = pessoaTeste.getId();
        
        // Obter primeira revisão
        List<Number> revisoes = enversAuditService.findRevisionsByPessoa(pessoaId);
        assertFalse(revisoes.isEmpty());
        Number primeiraRevisao = revisoes.get(0);

        // When
        Pessoa pessoaNaRevisao = enversAuditService.findPessoaAtRevision(pessoaId, primeiraRevisao);

        // Then
        assertNotNull(pessoaNaRevisao);
        assertEquals(nomeOriginal, pessoaNaRevisao.getNome());
        assertEquals(pessoaId, pessoaNaRevisao.getId());
    }

    @Test
    void testFindPessoasModifiedBetween_DeveFuncionarComPeriodo() {
        // Given
        Date inicio = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24h atrás
        Date fim = new Date(); // agora

        // When
        List<Pessoa> pessoasModificadas = enversAuditService.findPessoasModifiedBetween(inicio, fim);

        // Then
        assertNotNull(pessoasModificadas);
        // Deve incluir nossa pessoa de teste criada no período
        assertTrue(pessoasModificadas.stream()
            .anyMatch(p -> p.getId().equals(pessoaTeste.getId())));
    }

    @Test
    void testFindPessoaHistoryWithRevisionInfo_DeveRetornarHistoricoCompleto() {
        // Given
        Long pessoaId = pessoaTeste.getId();
        
        // Fazer uma modificação para ter mais histórico
        pessoaTeste.setNome("Nome Alterado");
        pessoaRepository.save(pessoaTeste);

        // When
        List<Object[]> historico = enversAuditService.findPessoaHistoryWithRevisionInfo(pessoaId);

        // Then
        assertNotNull(historico);
        assertFalse(historico.isEmpty());
        
        // Cada item do histórico deve ter pelo menos 3 elementos: entidade, revision info, revision type
        for (Object[] item : historico) {
            assertTrue(item.length >= 3);
            assertNotNull(item[0]); // Entidade Pessoa
            assertNotNull(item[1]); // Revision info
            assertNotNull(item[2]); // Revision type
        }
    }

    @Test
    void testFindAllPessoaOperations_DeveRetornarOperacoes() {
        // When
        List<Object[]> operacoes = enversAuditService.findAllPessoaOperations();

        // Then
        assertNotNull(operacoes);
        // Deve incluir pelo menos nossa operação de criação
        assertFalse(operacoes.isEmpty());
    }

    @Test
    void testServiceComPessoaInexistente_DeveRetornarResultadosVazios() {
        // Given
        Long idInexistente = 99999L;

        // When & Then
        List<Number> revisoes = enversAuditService.findRevisionsByPessoa(idInexistente);
        assertTrue(revisoes.isEmpty());

        Long totalRevisoes = enversAuditService.countPessoaRevisions(idInexistente);
        assertEquals(0L, totalRevisoes);

        Date ultimaModificacao = enversAuditService.findLastModificationDate(idInexistente);
        assertNull(ultimaModificacao);
    }
}
