package com.emiteai.integration;

import com.emiteai.entities.Auditoria;
import com.emiteai.repository.AuditoriaRepository;
import com.emiteai.service.AuditoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class AuditoriaIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Test
    void testAuditoriaEndpointAccessibility() {
        String url = "http://localhost:" + port + "/api/auditoria/health";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    void testAuditoriaStatisticsEndpoint() {
        String url = "http://localhost:" + port + "/api/auditoria/estatisticas?horas=1";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("totalRequisicoes"));
        assertTrue(response.getBody().containsKey("distribuicaoStatus"));
        assertTrue(response.getBody().containsKey("periodoHoras"));
    }

    @Test
    void testBuscarAuditoriasRecentes() {
        String url = "http://localhost:" + port + "/api/auditoria/recentes?horas=24";
        
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    void testAuditoriaServiceDirectly() {
        Long count = auditoriaService.contarRequisicoesUltimasHoras(24);
        assertNotNull(count);
        assertTrue(count >= 0);

        List<Auditoria> auditorias = auditoriaService.buscarAuditoriasRecentes(1);
        assertNotNull(auditorias);
    }

    @Test
    void testAuditoriaRepositoryBasicOperations() {
        Auditoria auditoria = new Auditoria();
        auditoria.setTimestampRequisicao(LocalDateTime.now());
        auditoria.setMetodoHttp("GET");
        auditoria.setEndpoint("/api/test");
        auditoria.setIpOrigem("127.0.0.1");
        auditoria.setStatusResposta(200);
        auditoria.setTempoProcessamento(100L);

        Auditoria saved = auditoriaRepository.save(auditoria);
        assertNotNull(saved.getId());

        LocalDateTime since = LocalDateTime.now().minusHours(1);
        List<Auditoria> recent = auditoriaRepository.findRecentAudits(since);
        assertNotNull(recent);

        Long count = auditoriaRepository.countRequestsSince(since);
        assertNotNull(count);
        assertTrue(count >= 1);
    }
}
