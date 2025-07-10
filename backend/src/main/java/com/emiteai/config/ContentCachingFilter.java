package com.emiteai.config;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ContentCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Apenas aplicar para endpoints de API
        if (httpRequest.getRequestURI().startsWith("/api/")) {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

            try {
                chain.doFilter(requestWrapper, responseWrapper);
            } finally {
                // Importante: copiar o conteúdo de volta para a resposta original
                responseWrapper.copyBodyToResponse();
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
