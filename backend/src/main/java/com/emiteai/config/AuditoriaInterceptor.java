package com.emiteai.config;

import com.emiteai.service.AuditoriaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AuditoriaInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditoriaService auditoriaService;

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String REQUEST_DATA_ATTRIBUTE = "requestData";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        // Capturar dados da requisição para endpoints de API
        if (request.getRequestURI().startsWith("/api/")) {
            try {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("method", request.getMethod());
                requestData.put("uri", request.getRequestURI());
                requestData.put("queryString", request.getQueryString());
                requestData.put("headers", getHeadersMap(request));
                
                // Capturar body se for POST/PUT/PATCH
                if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) {
                    if (request instanceof ContentCachingRequestWrapper) {
                        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                        byte[] body = wrapper.getContentAsByteArray();
                        if (body.length > 0) {
                            requestData.put("body", new String(body, StandardCharsets.UTF_8));
                        }
                    }
                }
                
                request.setAttribute(REQUEST_DATA_ATTRIBUTE, requestData);
            } catch (Exception e) {
                log.warn("Erro ao capturar dados da requisição para auditoria: {}", e.getMessage());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        // Apenas auditar endpoints de API
        if (!request.getRequestURI().startsWith("/api/")) {
            return;
        }

        try {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            long tempoProcessamento = startTime != null ? System.currentTimeMillis() - startTime : 0;

            Object requestData = request.getAttribute(REQUEST_DATA_ATTRIBUTE);
            Object responseData = null;

            // Capturar dados da resposta se disponível
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] body = wrapper.getContentAsByteArray();
                if (body.length > 0) {
                    String responseBody = new String(body, StandardCharsets.UTF_8);
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("status", response.getStatus());
                    responseMap.put("body", responseBody);
                    responseMap.put("contentType", response.getContentType());
                    responseData = responseMap;
                }
            } else {
                // Se não temos o body, pelo menos registrar o status
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("status", response.getStatus());
                responseMap.put("contentType", response.getContentType());
                responseData = responseMap;
            }

            // Registrar auditoria de forma assíncrona
            auditoriaService.registrarRequisicao(request, response, requestData, responseData, tempoProcessamento, ex);

        } catch (Exception e) {
            log.error("Erro ao registrar auditoria: {}", e.getMessage(), e);
        }
    }

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            // Não capturar headers sensíveis
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        });
        return headers;
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerCase = headerName.toLowerCase();
        return lowerCase.contains("authorization") || 
               lowerCase.contains("cookie") || 
               lowerCase.contains("password") ||
               lowerCase.contains("token");
    }
}
