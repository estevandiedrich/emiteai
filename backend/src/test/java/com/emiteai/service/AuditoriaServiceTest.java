package com.emiteai.service;

import com.emiteai.entities.Auditoria;
import com.emiteai.repository.AuditoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditoriaService auditoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarRequisicao_DeveRegistrarComSucesso() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/pessoas");
        request.addHeader("User-Agent", "Test-Agent");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        Map<String, Object> dadosRequisicao = new HashMap<>();
        dadosRequisicao.put("nome", "Teste");

        Map<String, Object> dadosResposta = new HashMap<>();
        dadosResposta.put("id", 1L);

        when(objectMapper.writeValueAsString(dadosRequisicao)).thenReturn("{\"nome\":\"Teste\"}");
        when(objectMapper.writeValueAsString(dadosResposta)).thenReturn("{\"id\":1}");

        // When
        auditoriaService.registrarRequisicao(request, response, dadosRequisicao, dadosResposta, 100L, null);

        // Then
        verify(auditoriaRepository, timeout(1000)).save(any(Auditoria.class));
    }

    @Test
    void testRegistrarRequisicao_ComExcecao() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/pessoas");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(400);

        RuntimeException erro = new RuntimeException("Erro de teste");

        // When
        auditoriaService.registrarRequisicao(request, response, null, null, 200L, erro);

        // Then
        verify(auditoriaRepository, timeout(1000)).save(argThat(auditoria -> 
            auditoria.getErro() != null && auditoria.getErro().contains("RuntimeException: Erro de teste")));
    }

    @Test
    void testBuscarAuditoriasPorEndpoint() {
        // Given
        String endpoint = "/api/pessoas";
        List<Auditoria> auditorias = Arrays.asList(createSampleAuditoria(), createSampleAuditoria());
        when(auditoriaRepository.findByEndpointContainingIgnoreCase(endpoint)).thenReturn(auditorias);

        // When
        List<Auditoria> result = auditoriaService.buscarAuditoriasPorEndpoint(endpoint);

        // Then
        assertEquals(2, result.size());
        verify(auditoriaRepository).findByEndpointContainingIgnoreCase(endpoint);
    }

    @Test
    void testBuscarAuditoriasRecentes() {
        // Given
        int horas = 24;
        List<Auditoria> auditorias = Arrays.asList(createSampleAuditoria());
        when(auditoriaRepository.findRecentAudits(any(LocalDateTime.class))).thenReturn(auditorias);

        // When
        List<Auditoria> result = auditoriaService.buscarAuditoriasRecentes(horas);

        // Then
        assertEquals(1, result.size());
        verify(auditoriaRepository).findRecentAudits(any(LocalDateTime.class));
    }

    @Test
    void testBuscarAuditoriasPorPeriodo() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        List<Auditoria> auditorias = Arrays.asList(createSampleAuditoria());
        when(auditoriaRepository.findByTimestampRequisicaoBetween(inicio, fim)).thenReturn(auditorias);

        // When
        List<Auditoria> result = auditoriaService.buscarAuditoriasPorPeriodo(inicio, fim);

        // Then
        assertEquals(1, result.size());
        verify(auditoriaRepository).findByTimestampRequisicaoBetween(inicio, fim);
    }

    @Test
    void testContarRequisicoesUltimasHoras() {
        // Given
        int horas = 12;
        Long expectedCount = 10L;
        when(auditoriaRepository.countRequestsSince(any(LocalDateTime.class))).thenReturn(expectedCount);

        // When
        Long result = auditoriaService.contarRequisicoesUltimasHoras(horas);

        // Then
        assertEquals(expectedCount, result);
        verify(auditoriaRepository).countRequestsSince(any(LocalDateTime.class));
    }

    @Test
    void testObterEstatisticasStatus() {
        // Given
        int horas = 6;
        List<Object[]> stats = Arrays.asList(
            new Object[]{200, 50L},
            new Object[]{400, 5L},
            new Object[]{500, 1L}
        );
        when(auditoriaRepository.getStatusStatistics(any(LocalDateTime.class))).thenReturn(stats);

        // When
        List<Object[]> result = auditoriaService.obterEstatisticasStatus(horas);

        // Then
        assertEquals(3, result.size());
        verify(auditoriaRepository).getStatusStatistics(any(LocalDateTime.class));
    }

    @Test
    void testRegistrarRequisicao_ComErroNaSerializacao() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("192.168.1.1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        Object dadosProblematicos = new HashMap<>();
        when(objectMapper.writeValueAsString(dadosProblematicos))
            .thenThrow(new RuntimeException("Erro de serialização"));

        // When
        auditoriaService.registrarRequisicao(request, response, dadosProblematicos, null, 50L, null);

        // Then
        verify(auditoriaRepository, timeout(1000)).save(argThat(auditoria -> 
            auditoria.getDadosRequisicao() != null && 
            auditoria.getDadosRequisicao().contains("Erro ao serializar dados da requisição")));
    }

    @Test
    void testRegistrarRequisicao_ComXForwardedForHeader() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");
        request.addHeader("X-Forwarded-For", "203.0.113.1, 192.168.1.1");
        request.setRemoteAddr("10.0.0.1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        // When
        auditoriaService.registrarRequisicao(request, response, null, null, 30L, null);

        // Then
        verify(auditoriaRepository, timeout(1000)).save(argThat(auditoria -> 
            "203.0.113.1".equals(auditoria.getIpOrigem())));
    }

    private Auditoria createSampleAuditoria() {
        Auditoria auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setMetodoHttp("GET");
        auditoria.setEndpoint("/api/pessoas");
        auditoria.setIpOrigem("127.0.0.1");
        auditoria.setStatusResposta(200);
        auditoria.setTempoProcessamento(100L);
        return auditoria;
    }
}
