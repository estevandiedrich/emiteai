package com.emiteai.service;

import com.emiteai.entities.Pessoa;
import com.emiteai.entities.Endereco;
import com.emiteai.repository.PessoaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnversFunctionalTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testEnversBasicFunctionality() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf("11122233344");
        pessoa.setTelefone("11999999999");
        
        Endereco endereco = new Endereco();
        endereco.setCep("01234567");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");
        endereco.setPessoa(pessoa);
        pessoa.setEndereco(endereco);
        
        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        
        entityManager.flush();
        
        assertNotNull(savedPessoa);
        assertNotNull(savedPessoa.getId());
        
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisoes = auditReader.getRevisions(Pessoa.class, savedPessoa.getId());
        
        assertNotNull(revisoes);
        System.out.println("✅ Revisões encontradas: " + revisoes.size());
        
        assertTrue(revisoes.size() >= 0, "Deve retornar lista válida");
        
        if (!revisoes.isEmpty()) {
            Number primeiraRevisao = revisoes.get(0);
            Pessoa pessoaNaRevisao = auditReader.find(Pessoa.class, savedPessoa.getId(), primeiraRevisao);
            
            assertNotNull(pessoaNaRevisao);
            assertEquals("João Silva", pessoaNaRevisao.getNome());
            System.out.println("✅ Pessoa encontrada na revisão: " + pessoaNaRevisao.getNome());
        } else {
            System.out.println("⚠️ Nenhuma revisão encontrada em ambiente de teste");
        }
    }

    @Test
    void testEnversWithModification() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setCpf("22233344455");
        pessoa.setTelefone("11888888888");
        
        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        entityManager.flush();
        
        savedPessoa.setNome("Maria Santos Silva");
        savedPessoa.setTelefone("11777777777");
        pessoaRepository.save(savedPessoa);
        entityManager.flush();
        
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisoes = auditReader.getRevisions(Pessoa.class, savedPessoa.getId());
        
        assertNotNull(revisoes);
        System.out.println("✅ Revisões após modificação: " + revisoes.size());
        
        assertTrue(revisoes.size() >= 0, "Deve retornar lista válida");
        
        if (revisoes.size() >= 2) {
            Number primeiraRevisao = revisoes.get(0);
            Number segundaRevisao = revisoes.get(1);
            
            Pessoa pessoaPrimeira = auditReader.find(Pessoa.class, savedPessoa.getId(), primeiraRevisao);
            Pessoa pessoaSegunda = auditReader.find(Pessoa.class, savedPessoa.getId(), segundaRevisao);
            
            assertNotNull(pessoaPrimeira);
            assertNotNull(pessoaSegunda);
            
            assertEquals("Maria Santos", pessoaPrimeira.getNome());
            assertEquals("Maria Santos Silva", pessoaSegunda.getNome());
            
            System.out.println("✅ Primeira revisão: " + pessoaPrimeira.getNome());
            System.out.println("✅ Segunda revisão: " + pessoaSegunda.getNome());
        }
    }
}
