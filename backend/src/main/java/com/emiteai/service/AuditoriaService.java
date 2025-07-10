package com.emiteai.service;

import com.emiteai.entities.Auditoria;
import com.emiteai.repository.AuditoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Async
    public void registrarRequisicao(HttpServletRequest request, HttpServletResponse response, 
                                   Object dadosRequisicao, Object dadosResposta, 
                                   long tempoProcessamento, Exception erro) {
        try {
            Auditoria auditoria = new Auditoria();
            
            // Dados da requisição
            auditoria.setMetodoHttp(request.getMethod());
            auditoria.setEndpoint(request.getRequestURI());
            auditoria.setIpOrigem(obterIpReal(request));
            auditoria.setUserAgent(request.getHeader("User-Agent"));
            auditoria.setTempoProcessamento(tempoProcessamento);
            
            // Dados da resposta
            auditoria.setStatusResposta(response.getStatus());
            
            // Serializar dados da requisição
            if (dadosRequisicao != null) {
                try {
                    auditoria.setDadosRequisicao(objectMapper.writeValueAsString(dadosRequisicao));
                } catch (Exception e) {
                    auditoria.setDadosRequisicao("Erro ao serializar dados da requisição: " + e.getMessage());
                }
            }
            
            // Serializar dados da resposta
            if (dadosResposta != null) {
                try {
                    String responseSerialized = objectMapper.writeValueAsString(dadosResposta);
                    // Limitar tamanho da resposta para evitar problemas de banco
                    if (responseSerialized.length() > 5000) {
                        responseSerialized = responseSerialized.substring(0, 5000) + "... [TRUNCADO]";
                    }
                    auditoria.setDadosResposta(responseSerialized);
                } catch (Exception e) {
                    auditoria.setDadosResposta("Erro ao serializar dados da resposta: " + e.getMessage());
                }
            }
            
            // Informações de erro
            if (erro != null) {
                auditoria.setErro(erro.getClass().getSimpleName() + ": " + erro.getMessage());
            }
            
            auditoriaRepository.save(auditoria);
            
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria: {}", e.getMessage(), e);
        }
    }

    private String obterIpReal(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    public List<Auditoria> buscarAuditoriasPorEndpoint(String endpoint) {
        return auditoriaRepository.findByEndpointContainingIgnoreCase(endpoint);
    }

    public List<Auditoria> buscarAuditoriasRecentes(int horas) {
        LocalDateTime inicio = LocalDateTime.now().minusHours(horas);
        return auditoriaRepository.findRecentAudits(inicio);
    }

    public List<Auditoria> buscarAuditoriasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return auditoriaRepository.findByTimestampRequisicaoBetween(inicio, fim);
    }

    public Long contarRequisicoesUltimasHoras(int horas) {
        LocalDateTime inicio = LocalDateTime.now().minusHours(horas);
        return auditoriaRepository.countRequestsSince(inicio);
    }

    public List<Object[]> obterEstatisticasStatus(int horas) {
        LocalDateTime inicio = LocalDateTime.now().minusHours(horas);
        return auditoriaRepository.getStatusStatistics(inicio);
    }
}
