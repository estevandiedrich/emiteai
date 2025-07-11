package com.emiteai.entities;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Listener para capturar informações do contexto da requisição
 * e adicionar à entidade de revisão do Envers
 */
public class CustomRevisionListener implements RevisionListener {
    
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevision = (CustomRevisionEntity) revisionEntity;
        
        try {
            // Capturar informações da requisição HTTP atual
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            // Capturar IP de origem
            String ipOrigem = getClientIpAddress(request);
            customRevision.setIpOrigem(ipOrigem);
            
            // Capturar User-Agent
            String userAgent = request.getHeader("User-Agent");
            customRevision.setUserAgent(userAgent);
            
            // Capturar usuário (se disponível no contexto)
            String usuario = request.getHeader("X-User-Id"); // Exemplo: pode vir do JWT
            if (usuario == null) {
                usuario = "sistema"; // Valor padrão
            }
            customRevision.setUsuario(usuario);
            
            // Capturar motivo da mudança (se fornecido)
            String motivo = request.getHeader("X-Change-Reason");
            customRevision.setMotivo(motivo);
            
        } catch (Exception e) {
            // Se não conseguir capturar as informações (ex: contexto não-HTTP)
            // definir valores padrão
            customRevision.setUsuario("sistema");
            customRevision.setIpOrigem("unknown");
            customRevision.setUserAgent("unknown");
        }
    }
    
    /**
     * Método para capturar o IP real do cliente, considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Pegar o primeiro IP se houver múltiplos
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
