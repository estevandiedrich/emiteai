package com.emiteai.config;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro CORS adicional para casos específicos onde a configuração WebMvcConfigurer não é suficiente.
 * Por padrão está desabilitado, use apenas se necessário.
 */
//@Component // Desabilitado para evitar conflitos com AuditoriaConfig
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final String ALLOWED_ORIGINS = "http://localhost:3000,http://127.0.0.1:3000,http://localhost:80,http://frontend:80";
    private static final String ALLOWED_METHODS = "GET,POST,PUT,DELETE,PATCH,OPTIONS";
    private static final String ALLOWED_HEADERS = "Origin,X-Requested-With,Content-Type,Accept,Authorization,X-Forwarded-For,X-Forwarded-Proto";
    private static final String MAX_AGE = "3600";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        
        // Verificar se a origem está permitida
        if (origin != null && isOriginAllowed(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        httpResponse.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        httpResponse.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", MAX_AGE);
        
        // Responder a requisições OPTIONS (CORS preflight)
        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isOriginAllowed(String origin) {
        String[] allowedOrigins = ALLOWED_ORIGINS.split(",");
        for (String allowedOrigin : allowedOrigins) {
            if (allowedOrigin.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro se necessário
    }
    
    @Override
    public void destroy() {
        // Limpeza do filtro se necessário
    }
}