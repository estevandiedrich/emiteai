package com.emiteai.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@Profile("docker")
public class ManualFlywayConfig {
    
    public ManualFlywayConfig() {
        System.out.println("🔧 ManualFlywayConfig instantiated!");
    }
    
    @Autowired
    private DataSource dataSource;
    
    @PostConstruct
    public void migrate() {
        System.out.println("🔄 Starting manual Flyway migration...");
        
        try {
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
            
            System.out.println("🔍 Flyway info:");
            for (var info : flyway.info().all()) {
                System.out.println("  Migration: " + info.getVersion() + " - " + info.getDescription() + " - " + info.getState());
            }
            
            flyway.migrate();
            System.out.println("✅ Manual Flyway migration completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Manual Flyway migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
