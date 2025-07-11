package com.emiteai.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EntityManagerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testEntityManagerInjection() {
        // Verificar se o EntityManager foi injetado corretamente
        assertNotNull(entityManager, "EntityManager should be injected");
        System.out.println("✅ EntityManager foi injetado com sucesso!");
        
        // Verificar se conseguimos acessar o Envers
        try {
            // Tentar importar e usar as classes do Envers
            Class.forName("org.hibernate.envers.AuditReaderFactory");
            Class.forName("org.hibernate.envers.AuditReader");
            System.out.println("✅ Classes do Envers estão disponíveis!");
        } catch (ClassNotFoundException e) {
            fail("Envers classes should be available: " + e.getMessage());
        }
    }
}
