package com.emiteai.dtos;

import com.emiteai.entities.Auditoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditoriaDTOTest {

    @Test
    void testConstrucaoComEntidade() {
        // Given
        Auditoria auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setTimestampRequisicao(LocalDateTime.of(2025, 1, 1, 12, 0));
        auditoria.setMetodoHttp("GET");
        auditoria.setEndpoint("/api/test");
        auditoria.setIpOrigem("127.0.0.1");
        auditoria.setUserAgent("Test-Agent");
        auditoria.setDadosRequisicao("{\"test\":\"data\"}");
        auditoria.setStatusResposta(200);
        auditoria.setDadosResposta("{\"result\":\"ok\"}");
        auditoria.setTempoProcessamento(100L);
        auditoria.setErro(null);
        auditoria.setUsuarioIdentificado("testuser");

        // When
        AuditoriaDTO dto = new AuditoriaDTO(auditoria);

        // Then
        assertEquals(1L, dto.getId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), dto.getTimestampRequisicao());
        assertEquals("GET", dto.getMetodoHttp());
        assertEquals("/api/test", dto.getEndpoint());
        assertEquals("127.0.0.1", dto.getIpOrigem());
        assertEquals("Test-Agent", dto.getUserAgent());
        assertEquals("{\"test\":\"data\"}", dto.getDadosRequisicao());
        assertEquals(200, dto.getStatusResposta());
        assertEquals("{\"result\":\"ok\"}", dto.getDadosResposta());
        assertEquals(100L, dto.getTempoProcessamento());
        assertNull(dto.getErro());
        assertEquals("testuser", dto.getUsuarioIdentificado());
    }

    @Test
    void testConstrucaoVazia() {
        // When
        AuditoriaDTO dto = new AuditoriaDTO();

        // Then
        assertNull(dto.getId());
        assertNull(dto.getTimestampRequisicao());
        assertNull(dto.getMetodoHttp());
        assertNull(dto.getEndpoint());
        assertNull(dto.getIpOrigem());
        assertNull(dto.getUserAgent());
        assertNull(dto.getDadosRequisicao());
        assertNull(dto.getStatusResposta());
        assertNull(dto.getDadosResposta());
        assertNull(dto.getTempoProcessamento());
        assertNull(dto.getErro());
        assertNull(dto.getUsuarioIdentificado());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        AuditoriaDTO dto = new AuditoriaDTO();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        dto.setId(2L);
        dto.setTimestampRequisicao(timestamp);
        dto.setMetodoHttp("POST");
        dto.setEndpoint("/api/pessoas");
        dto.setIpOrigem("192.168.1.100");
        dto.setUserAgent("Custom-Agent");
        dto.setDadosRequisicao("{\"nome\":\"João\"}");
        dto.setStatusResposta(201);
        dto.setDadosResposta("{\"id\":2}");
        dto.setTempoProcessamento(250L);
        dto.setErro("Erro de validação");
        dto.setUsuarioIdentificado("admin");

        // Then
        assertEquals(2L, dto.getId());
        assertEquals(timestamp, dto.getTimestampRequisicao());
        assertEquals("POST", dto.getMetodoHttp());
        assertEquals("/api/pessoas", dto.getEndpoint());
        assertEquals("192.168.1.100", dto.getIpOrigem());
        assertEquals("Custom-Agent", dto.getUserAgent());
        assertEquals("{\"nome\":\"João\"}", dto.getDadosRequisicao());
        assertEquals(201, dto.getStatusResposta());
        assertEquals("{\"id\":2}", dto.getDadosResposta());
        assertEquals(250L, dto.getTempoProcessamento());
        assertEquals("Erro de validação", dto.getErro());
        assertEquals("admin", dto.getUsuarioIdentificado());
    }

    @Test
    void testConstrucaoComEntidadeComValoresNulos() {
        // Given
        Auditoria auditoria = new Auditoria();
        auditoria.setId(3L);
        auditoria.setMetodoHttp("DELETE");
        auditoria.setEndpoint("/api/test/3");
        // Deixar outros campos nulos propositalmente

        // When
        AuditoriaDTO dto = new AuditoriaDTO(auditoria);

        // Then
        assertEquals(3L, dto.getId());
        assertEquals("DELETE", dto.getMetodoHttp());
        assertEquals("/api/test/3", dto.getEndpoint());
        assertNull(dto.getIpOrigem());
        assertNull(dto.getUserAgent());
        assertNull(dto.getDadosRequisicao());
        assertNull(dto.getStatusResposta());
        assertNull(dto.getDadosResposta());
        assertNull(dto.getTempoProcessamento());
        assertNull(dto.getErro());
        assertNull(dto.getUsuarioIdentificado());
    }

    @Test
    void testConstrucaoComDadosLongos() {
        // Given
        Auditoria auditoria = new Auditoria();
        String longText = "x".repeat(5000);
        
        auditoria.setId(4L);
        auditoria.setMetodoHttp("PUT");
        auditoria.setEndpoint("/api/long-data");
        auditoria.setDadosRequisicao(longText);
        auditoria.setDadosResposta(longText);
        auditoria.setErro(longText);

        // When
        AuditoriaDTO dto = new AuditoriaDTO(auditoria);

        // Then
        assertEquals(4L, dto.getId());
        assertEquals("PUT", dto.getMetodoHttp());
        assertEquals("/api/long-data", dto.getEndpoint());
        assertEquals(longText, dto.getDadosRequisicao());
        assertEquals(longText, dto.getDadosResposta());
        assertEquals(longText, dto.getErro());
    }
}
