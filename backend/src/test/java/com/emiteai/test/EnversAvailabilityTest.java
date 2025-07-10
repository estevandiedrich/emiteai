package com.emiteai.test;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class EnversAvailabilityTest {
    
    @Test
    public void testEnversClassesAvailable() {
        try {
            // Verificar se as classes do Envers estão disponíveis
            Class.forName("org.hibernate.envers.Audited");
            Class.forName("org.hibernate.envers.AuditReader");
            Class.forName("org.hibernate.envers.AuditReaderFactory");
            
            System.out.println("✅ Envers classes are available");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Envers classes not found: " + e.getMessage());
            throw new RuntimeException("Envers not available", e);
        }
    }
}
