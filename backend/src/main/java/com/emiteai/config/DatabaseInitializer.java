package com.emiteai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Componente para verificar e criar databases automaticamente na inicialização
 */
@Component
@Profile("!test") // Não executar em testes
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public void run(String... args) throws Exception {
        createDatabaseIfNotExists("emiteai");
        createDatabaseIfNotExists("inventory-db");
    }

    private void createDatabaseIfNotExists(String databaseName) {
        try {
            // Conectar ao database 'postgres' para verificar/criar outros databases
            String postgresUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/")) + "/postgres";
            
            try (Connection connection = DriverManager.getConnection(postgresUrl, datasourceUsername, datasourcePassword);
                 Statement statement = connection.createStatement()) {
                
                // Verificar se o database existe
                String checkQuery = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";
                ResultSet resultSet = statement.executeQuery(checkQuery);
                
                if (!resultSet.next()) {
                    // Database não existe, criar
                    String createQuery = "CREATE DATABASE \"" + databaseName + "\"";
                    statement.executeUpdate(createQuery);
                    System.out.println("✅ Database '" + databaseName + "' criado com sucesso!");
                } else {
                    System.out.println("ℹ️  Database '" + databaseName + "' já existe");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar database '" + databaseName + "': " + e.getMessage());
            // Não falhar a aplicação por causa disso
        }
    }
}
