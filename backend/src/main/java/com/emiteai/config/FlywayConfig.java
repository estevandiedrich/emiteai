package com.emiteai.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuração do Flyway para criação automática de databases
 */
@Configuration
@Profile("!test") // Não aplicar em testes
public class FlywayConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Configuração do Flyway para garantir que o database seja criado
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .createSchemas(true)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(true)
                .locations("classpath:db/migration")
                .load();
    }

    /**
     * Flyway para database alternativo (inventory-db) se necessário
     */
    @Bean
    public Flyway inventoryFlyway() {
        String inventoryUrl = datasourceUrl.replace("/emiteai", "/inventory-db");
        
        return Flyway.configure()
                .dataSource(inventoryUrl, datasourceUsername, datasourcePassword)
                .createSchemas(true)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(true)
                .locations("classpath:db/migration")
                .load();
    }
}
