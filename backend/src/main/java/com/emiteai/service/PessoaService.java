package com.emiteai.service;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public PessoaDTO cadastrarPessoa(PessoaDTO pessoaDTO) {
        if (pessoaRepository.existsByCpf(pessoaDTO.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(pessoaDTO.getNome());
        pessoa.setTelefone(pessoaDTO.getTelefone());
        pessoa.setCpf(pessoaDTO.getCpf());
        pessoaRepository.save(pessoa);
        return new PessoaDTO(pessoa);
    }

    public List<PessoaDTO> listarTodas() {
        return pessoaRepository.findAll()
            .stream()
            .map(PessoaDTO::new)
            .collect(Collectors.toList());
    }

    public PessoaDTO buscarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return new PessoaDTO(pessoa);
    }

    public PessoaDTO buscarPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return new PessoaDTO(pessoa);
    }

    public PessoaDTO atualizarPessoa(Long id, PessoaDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setCpf(dto.getCpf());

        pessoaRepository.save(pessoa);

        return new PessoaDTO(pessoa);
    }

    public void deletar(Long id) {
        pessoaRepository.deleteById(id);
    }
}
