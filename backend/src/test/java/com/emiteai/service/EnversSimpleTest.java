package com.emiteai.service;

import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnversSimpleTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EnversAuditService enversAuditService;

    @Test
    void testEnversClassesAvailable() {
        // Verificar se as classes do Envers estão disponíveis
        try {
            Class.forName("org.hibernate.envers.Audited");
            Class.forName("org.hibernate.envers.AuditReader");
            Class.forName("org.hibernate.envers.AuditReaderFactory");
            System.out.println("✅ Envers classes are available");
        } catch (ClassNotFoundException e) {
            fail("Envers classes not found: " + e.getMessage());
        }
    }

    @Test
    void testEnversServiceInjection() {
        // Verificar se o serviço foi injetado corretamente
        assertNotNull(enversAuditService, "EnversAuditService should be injected");
        System.out.println("✅ EnversAuditService foi injetado com sucesso!");
    }

    @Test
    void testEnversBasicFunctionality() {
        // Given - Criar uma pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        // Gerar CPF único simples
        String cpfUnico = "111" + String.valueOf(System.currentTimeMillis() % 100000000);
        pessoa.setCpf(cpfUnico);
        pessoa.setTelefone("11999999999");
        
        // When - Salvar a pessoa
        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        
        // Then - Verificar se a pessoa foi salva
        assertNotNull(savedPessoa);
        assertNotNull(savedPessoa.getId());
        
        // Verificar se o serviço Envers funciona (pode retornar lista vazia, mas não deve dar erro)
        try {
            var revisoes = enversAuditService.findRevisionsByPessoa(savedPessoa.getId());
            assertNotNull(revisoes);
            System.out.println("✅ Envers service working - Revisões encontradas: " + revisoes.size());
        } catch (Exception e) {
            System.out.println("❌ Erro no Envers service: " + e.getMessage());
            fail("Envers service should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testEnversWithModification() {
        // Given - Criar uma pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        // Usar CPF fixo válido
        pessoa.setCpf("44455566677");
        pessoa.setTelefone("11888888888");
        
        // When - Salvar a pessoa
        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        
        // Modificar a pessoa
        savedPessoa.setNome("Maria Santos Silva");
        Pessoa updatedPessoa = pessoaRepository.save(savedPessoa);
        
        // Then - Verificar se funcionou
        assertNotNull(updatedPessoa);
        assertEquals("Maria Santos Silva", updatedPessoa.getNome());
        
        // Verificar revisões
        try {
            var revisoes = enversAuditService.findRevisionsByPessoa(savedPessoa.getId());
            assertNotNull(revisoes);
            System.out.println("✅ Revisões após modificação: " + revisoes.size());
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar revisões: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
        }
    }
}
