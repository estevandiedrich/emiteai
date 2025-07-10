package com.emiteai.controller;

import com.emiteai.entities.Auditoria;
import com.emiteai.service.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditoriaController.class)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaService auditoriaService;

    private Auditoria sampleAuditoria;

    @BeforeEach
    void setUp() {
        sampleAuditoria = new Auditoria();
        sampleAuditoria.setId(1L);
        sampleAuditoria.setTimestampRequisicao(LocalDateTime.now());
        sampleAuditoria.setMetodoHttp("POST");
        sampleAuditoria.setEndpoint("/api/pessoas");
        sampleAuditoria.setIpOrigem("127.0.0.1");
        sampleAuditoria.setStatusResposta(200);
        sampleAuditoria.setTempoProcessamento(150L);
    }

    @Test
    void testBuscarAuditoriasRecentes_DeveRetornarListaDeAuditorias() throws Exception {
        // Given
        List<Auditoria> auditorias = Arrays.asList(sampleAuditoria);
        when(auditoriaService.buscarAuditoriasRecentes(24)).thenReturn(auditorias);

        // When & Then
        mockMvc.perform(get("/api/auditoria/recentes")
                .param("horas", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].metodoHttp").value("POST"))
                .andExpect(jsonPath("$[0].endpoint").value("/api/pessoas"));

        verify(auditoriaService).buscarAuditoriasRecentes(24);
    }

    @Test
    void testBuscarAuditoriasRecentes_ComParametroPadrao() throws Exception {
        // Given
        when(auditoriaService.buscarAuditoriasRecentes(24)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/auditoria/recentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(auditoriaService).buscarAuditoriasRecentes(24);
    }

    @Test
    void testBuscarPorEndpoint_DeveRetornarAuditoriasFiltradas() throws Exception {
        // Given
        String endpoint = "/api/pessoas";
        List<Auditoria> auditorias = Arrays.asList(sampleAuditoria);
        when(auditoriaService.buscarAuditoriasPorEndpoint(endpoint)).thenReturn(auditorias);

        // When & Then
        mockMvc.perform(get("/api/auditoria/endpoint")
                .param("endpoint", endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].endpoint").value(endpoint));

        verify(auditoriaService).buscarAuditoriasPorEndpoint(endpoint);
    }

    @Test
    void testBuscarPorPeriodo_DeveRetornarAuditoriasDoPeriodo() throws Exception {
        // Given
        List<Auditoria> auditorias = Arrays.asList(sampleAuditoria);
        when(auditoriaService.buscarAuditoriasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(auditorias);

        // When & Then
        mockMvc.perform(get("/api/auditoria/periodo")
                .param("inicio", "2025-01-01T00:00:00")
                .param("fim", "2025-01-02T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(auditoriaService).buscarAuditoriasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testObterEstatisticas_DeveRetornarEstatisticasCompletas() throws Exception {
        // Given
        Long totalRequisicoes = 100L;
        List<Object[]> statusStats = Arrays.asList(
            new Object[]{200, 80L},
            new Object[]{400, 15L},
            new Object[]{500, 5L}
        );

        when(auditoriaService.contarRequisicoesUltimasHoras(24)).thenReturn(totalRequisicoes);
        when(auditoriaService.obterEstatisticasStatus(24)).thenReturn(statusStats);

        // When & Then
        mockMvc.perform(get("/api/auditoria/estatisticas")
                .param("horas", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequisicoes").value(100))
                .andExpect(jsonPath("$.distribuicaoStatus").exists())
                .andExpect(jsonPath("$.distribuicaoStatus.200").value(80))
                .andExpect(jsonPath("$.distribuicaoStatus.400").value(15))
                .andExpect(jsonPath("$.distribuicaoStatus.500").value(5))
                .andExpect(jsonPath("$.periodoHoras").value(24));

        verify(auditoriaService).contarRequisicoesUltimasHoras(24);
        verify(auditoriaService).obterEstatisticasStatus(24);
    }

    @Test
    void testObterEstatisticas_ComParametroPadrao() throws Exception {
        // Given
        when(auditoriaService.contarRequisicoesUltimasHoras(24)).thenReturn(50L);
        when(auditoriaService.obterEstatisticasStatus(24)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/auditoria/estatisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequisicoes").value(50))
                .andExpect(jsonPath("$.periodoHoras").value(24));

        verify(auditoriaService).contarRequisicoesUltimasHoras(24);
        verify(auditoriaService).obterEstatisticasStatus(24);
    }

    @Test
    void testLimparAuditoriasAntigas_DeveRetornarInformacoes() throws Exception {
        // Given
        List<Auditoria> auditoriasAntigas = Arrays.asList(sampleAuditoria, sampleAuditoria);
        when(auditoriaService.buscarAuditoriasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(auditoriasAntigas);

        // When & Then
        mockMvc.perform(delete("/api/auditoria/limpar")
                .param("dias", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrosIdentificados").value(2))
                .andExpect(jsonPath("$.observacao").exists());

        verify(auditoriaService).buscarAuditoriasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testHealthCheck_DeveRetornarStatusUP() throws Exception {
        // Given
        when(auditoriaService.contarRequisicoesUltimasHoras(1)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/auditoria/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.requisicoesUltimaHora").value(5));

        verify(auditoriaService).contarRequisicoesUltimasHoras(1);
    }

    @Test
    void testHealthCheck_ComExcecao_DeveRetornarStatusDOWN() throws Exception {
        // Given
        when(auditoriaService.contarRequisicoesUltimasHoras(1))
            .thenThrow(new RuntimeException("Erro de conexão"));

        // When & Then
        mockMvc.perform(get("/api/auditoria/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.erro").value("Erro de conexão"));

        verify(auditoriaService).contarRequisicoesUltimasHoras(1);
    }
}
