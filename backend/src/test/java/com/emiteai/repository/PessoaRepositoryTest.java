package com.emiteai.repository;

import com.emiteai.entities.Pessoa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PessoaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Test
    void testFindByCpf_WhenPessoaExists() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setTelefone("(11) 99999-9999");
        pessoa.setCpf("123.456.789-00");
        entityManager.persistAndFlush(pessoa);

        Optional<Pessoa> result = pessoaRepository.findByCpf("123.456.789-00");

        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getNome());
        assertEquals("123.456.789-00", result.get().getCpf());
    }

    @Test
    void testFindByCpf_WhenPessoaNotExists() {
        Optional<Pessoa> result = pessoaRepository.findByCpf("999.999.999-99");

        assertTrue(result.isEmpty());
    }

    @Test
    void testExistsByCpf_WhenPessoaExists() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setCpf("987.654.321-00");
        entityManager.persistAndFlush(pessoa);

        boolean exists = pessoaRepository.existsByCpf("987.654.321-00");

        assertTrue(exists);
    }

    @Test
    void testExistsByCpf_WhenPessoaNotExists() {
        boolean exists = pessoaRepository.existsByCpf("111.111.111-11");

        assertFalse(exists);
    }

    @Test
    void testSave_NewPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Carlos Oliveira");
        pessoa.setTelefone("(11) 88888-8888");
        pessoa.setCpf("456.789.123-00");

        Pessoa savedPessoa = pessoaRepository.save(pessoa);

        assertNotNull(savedPessoa.getId());
        assertEquals("Carlos Oliveira", savedPessoa.getNome());
        assertEquals("456.789.123-00", savedPessoa.getCpf());
    }

    @Test
    void testFindAll() {
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("Pessoa 1");
        pessoa1.setCpf("111.111.111-11");

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Pessoa 2");
        pessoa2.setCpf("222.222.222-22");

        entityManager.persistAndFlush(pessoa1);
        entityManager.persistAndFlush(pessoa2);

        var pessoas = pessoaRepository.findAll();

        assertEquals(2, pessoas.size());
    }

    @Test
    void testDeleteById() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Pessoa para deletar");
        pessoa.setCpf("333.333.333-33");
        pessoa = entityManager.persistAndFlush(pessoa);

        pessoaRepository.deleteById(pessoa.getId());

        Optional<Pessoa> deletedPessoa = pessoaRepository.findById(pessoa.getId());
        assertTrue(deletedPessoa.isEmpty());
    }
}
