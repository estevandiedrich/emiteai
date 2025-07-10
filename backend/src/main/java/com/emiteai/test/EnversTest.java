package com.emiteai.test;

// Teste simples para verificar se o Envers está disponível
public class EnversTest {
    
    public void testEnversImports() {
        // Esta classe é apenas para testar se conseguimos importar as classes do Envers
        try {
            Class.forName("org.hibernate.envers.Audited");
            Class.forName("org.hibernate.envers.AuditReader");
            Class.forName("org.hibernate.envers.AuditReaderFactory");
            System.out.println("✅ Envers dependencies are available");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Envers dependencies not found: " + e.getMessage());
        }
    }
}
