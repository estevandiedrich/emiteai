package com.emiteai.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;

@Component
public class StartupDebugger implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 === STARTUP DEBUG INFO ===");
        
        // Check active profiles
        System.out.println("📋 Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
        
        // Check Flyway configuration
        String flywayEnabled = environment.getProperty("spring.flyway.enabled");
        System.out.println("🔧 Flyway enabled: " + flywayEnabled);
        
        String flywayLocations = environment.getProperty("spring.flyway.locations");
        System.out.println("📁 Flyway locations: " + flywayLocations);
        
        // Check if Flyway beans exist
        try {
            Object flyway = applicationContext.getBean("flyway");
            System.out.println("✅ Flyway bean found: " + flyway.getClass().getName());
        } catch (Exception e) {
            System.out.println("❌ Flyway bean not found: " + e.getMessage());
        }
        
        // Check FlywayMigrationStrategy
        try {
            Object flywayStrategy = applicationContext.getBean("flywayMigrationStrategy");
            System.out.println("✅ FlywayMigrationStrategy bean found: " + flywayStrategy.getClass().getName());
        } catch (Exception e) {
            System.out.println("❌ FlywayMigrationStrategy bean not found: " + e.getMessage());
        }
        
        // Check database connection and tables
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("🗄️  Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            
            // List tables
            ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            System.out.println("📊 Existing tables:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("  - " + tableName);
            }
            tables.close();
        } catch (Exception e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
        }
        
        System.out.println("🔍 === END DEBUG INFO ===");
    }
}
