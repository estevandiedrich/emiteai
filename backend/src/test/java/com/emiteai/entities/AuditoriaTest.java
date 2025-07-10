package com.emiteai.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditoriaTest {

    @Test
    void testCriacaoAuditoria_DeveInicializarTimestamp() {
        // When
        Auditoria auditoria = new Auditoria();

        // Then
        assertNotNull(auditoria.getTimestampRequisicao());
        assertTrue(auditoria.getTimestampRequisicao().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(auditoria.getTimestampRequisicao().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Auditoria auditoria = new Auditoria();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        auditoria.setId(1L);
        auditoria.setTimestampRequisicao(timestamp);
        auditoria.setMetodoHttp("POST");
        auditoria.setEndpoint("/api/pessoas");
        auditoria.setIpOrigem("192.168.1.1");
        auditoria.setUserAgent("Mozilla/5.0");
        auditoria.setDadosRequisicao("{\"nome\":\"Test\"}");
        auditoria.setStatusResposta(200);
        auditoria.setDadosResposta("{\"id\":1}");
        auditoria.setTempoProcessamento(150L);
        auditoria.setErro("Nenhum erro");
        auditoria.setUsuarioIdentificado("user@test.com");

        // Then
        assertEquals(1L, auditoria.getId());
        assertEquals(timestamp, auditoria.getTimestampRequisicao());
        assertEquals("POST", auditoria.getMetodoHttp());
        assertEquals("/api/pessoas", auditoria.getEndpoint());
        assertEquals("192.168.1.1", auditoria.getIpOrigem());
        assertEquals("Mozilla/5.0", auditoria.getUserAgent());
        assertEquals("{\"nome\":\"Test\"}", auditoria.getDadosRequisicao());
        assertEquals(200, auditoria.getStatusResposta());
        assertEquals("{\"id\":1}", auditoria.getDadosResposta());
        assertEquals(150L, auditoria.getTempoProcessamento());
        assertEquals("Nenhum erro", auditoria.getErro());
        assertEquals("user@test.com", auditoria.getUsuarioIdentificado());
    }

    @Test
    void testAuditoria_ComDadosNulos() {
        // Given
        Auditoria auditoria = new Auditoria();

        // When & Then
        assertDoesNotThrow(() -> {
            auditoria.setDadosRequisicao(null);
            auditoria.setDadosResposta(null);
            auditoria.setErro(null);
            auditoria.setUsuarioIdentificado(null);
            auditoria.setUserAgent(null);
            auditoria.setIpOrigem(null);
        });

        assertNull(auditoria.getDadosRequisicao());
        assertNull(auditoria.getDadosResposta());
        assertNull(auditoria.getErro());
        assertNull(auditoria.getUsuarioIdentificado());
        assertNull(auditoria.getUserAgent());
        assertNull(auditoria.getIpOrigem());
    }

    @Test
    void testAuditoria_ComLongText() {
        // Given
        Auditoria auditoria = new Auditoria();
        String longText = "a".repeat(10000); // Texto longo

        // When
        auditoria.setDadosRequisicao(longText);
        auditoria.setDadosResposta(longText);
        auditoria.setErro(longText);

        // Then
        assertEquals(longText, auditoria.getDadosRequisicao());
        assertEquals(longText, auditoria.getDadosResposta());
        assertEquals(longText, auditoria.getErro());
    }

    @Test
    void testAuditoria_TimestampDefault() {
        // Given
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        
        // When
        Auditoria auditoria = new Auditoria();
        
        // Then
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);
        assertNotNull(auditoria.getTimestampRequisicao());
        assertTrue(auditoria.getTimestampRequisicao().isAfter(antes));
        assertTrue(auditoria.getTimestampRequisicao().isBefore(depois));
    }
}
