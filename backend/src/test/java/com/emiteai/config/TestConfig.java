package com.emiteai.config;

import com.emiteai.service.AuditoriaService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de teste para resolver problemas de dependência nos testes @WebMvcTest
 */
@TestConfiguration
public class TestConfig implements WebMvcConfigurer {

    @MockBean
    private AuditoriaService auditoriaService;

    @Bean
    @Primary
    public WebMvcConfigurer testWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // Não adicionar interceptors nos testes para evitar dependências desnecessárias
            }
        };
    }
}
