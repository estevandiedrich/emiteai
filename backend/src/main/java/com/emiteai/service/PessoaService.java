package com.emiteai.service;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.entities.Pessoa;
import com.emiteai.entities.Endereco;
import com.emiteai.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public PessoaDTO cadastrarPessoa(PessoaDTO pessoaDTO) {
        if (pessoaRepository.existsByCpf(pessoaDTO.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(pessoaDTO.getNome());
        pessoa.setTelefone(pessoaDTO.getTelefone());
        pessoa.setCpf(pessoaDTO.getCpf());
        
        // Criar endereço se fornecido
        if (pessoaDTO.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(pessoaDTO.getEndereco().getCep());
            endereco.setNumero(pessoaDTO.getEndereco().getNumero());
            endereco.setComplemento(pessoaDTO.getEndereco().getComplemento());
            endereco.setBairro(pessoaDTO.getEndereco().getBairro());
            endereco.setMunicipio(pessoaDTO.getEndereco().getMunicipio());
            endereco.setEstado(pessoaDTO.getEndereco().getEstado());
            endereco.setPessoa(pessoa);
            pessoa.setEndereco(endereco);
        }
        
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

    @Transactional
    public PessoaDTO atualizarPessoa(Long id, PessoaDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setCpf(dto.getCpf());

        // Atualizar endereço
        if (dto.getEndereco() != null) {
            Endereco endereco = pessoa.getEndereco();
            if (endereco == null) {
                endereco = new Endereco();
                endereco.setPessoa(pessoa);
                pessoa.setEndereco(endereco);
            }
            
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setMunicipio(dto.getEndereco().getMunicipio());
            endereco.setEstado(dto.getEndereco().getEstado());
        }

        pessoaRepository.save(pessoa);

        return new PessoaDTO(pessoa);
    }

    public void deletar(Long id) {
        pessoaRepository.deleteById(id);
    }
}
