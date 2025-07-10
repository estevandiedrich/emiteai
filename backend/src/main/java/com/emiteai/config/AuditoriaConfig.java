package com.emiteai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class AuditoriaConfig implements WebMvcConfigurer {

    @Autowired
    private AuditoriaInterceptor auditoriaInterceptor;

    @Autowired
    private ContentCachingFilter contentCachingFilter;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(auditoriaInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auditoria/**"); // Evitar auditoria recursiva
    }

    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilterRegistration() {
        FilterRegistrationBean<ContentCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(contentCachingFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1); // Executar antes de outros filtros
        return registration;
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(false); // Headers são capturados pelo interceptor
        filter.setIncludePayload(false); // Payload é capturado pelo interceptor
        filter.setMaxPayloadLength(1000);
        return filter;
    }
}
