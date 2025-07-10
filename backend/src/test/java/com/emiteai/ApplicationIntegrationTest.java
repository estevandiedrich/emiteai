package com.emiteai;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class ApplicationIntegrationTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        pessoaRepository.deleteAll();
    }

    @Test
    void testCadastrarPessoa_IntegrationFlow() throws Exception {
        // Given
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("João Silva");
        pessoaDTO.setTelefone("(11) 99999-9999");
        pessoaDTO.setCpf("123.456.789-00");

        // When - Cadastrar pessoa
        mockMvc.perform(post("/api/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));

        // Then - Verificar se foi salvo no banco
        assertTrue(pessoaRepository.existsByCpf("123.456.789-00"));
        Pessoa savedPessoa = pessoaRepository.findByCpf("123.456.789-00").orElse(null);
        assertNotNull(savedPessoa);
        assertEquals("João Silva", savedPessoa.getNome());
    }

    @Test
    void testBuscarPessoa_IntegrationFlow() throws Exception {
        // Given - Salvar pessoa no banco
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setTelefone("(11) 88888-8888");
        pessoa.setCpf("987.654.321-00");
        pessoa = pessoaRepository.save(pessoa);

        // When & Then - Buscar por ID
        mockMvc.perform(get("/api/pessoas/{id}", pessoa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.cpf").value("987.654.321-00"));

        // When & Then - Buscar por CPF
        mockMvc.perform(get("/api/pessoas/cpf/{cpf}", "987.654.321-00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Santos"));
    }

    @Test
    void testListarPessoas_IntegrationFlow() throws Exception {
        // Given - Salvar múltiplas pessoas
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("João");
        pessoa1.setCpf("111.111.111-11");
        pessoaRepository.save(pessoa1);

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Maria");
        pessoa2.setCpf("222.222.222-22");
        pessoaRepository.save(pessoa2);

        // When & Then
        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[1].nome").exists());
    }

    @Test
    void testCadastrarPessoa_CpfDuplicado() throws Exception {
        // Given - Salvar pessoa com CPF
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Primeira Pessoa");
        pessoa.setCpf("555.555.555-55");
        pessoaRepository.save(pessoa);

        // When - Tentar cadastrar com mesmo CPF
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("Segunda Pessoa");
        pessoaDTO.setCpf("555.555.555-55");

        // Then - Deve retornar erro
        mockMvc.perform(post("/api/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscarPessoa_NaoEncontrada() throws Exception {
        // When & Then - Buscar pessoa que não existe
        mockMvc.perform(get("/api/pessoas/{id}", 999L))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/pessoas/cpf/{cpf}", "999.999.999-99"))
                .andExpect(status().isBadRequest());
    }
}
