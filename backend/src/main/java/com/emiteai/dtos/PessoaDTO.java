package com.emiteai.dtos;

import com.emiteai.entities.Pessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PessoaDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String cpf;

    public PessoaDTO(Pessoa pessoa) {
        this.id = pessoa.getId();
        this.nome = pessoa.getNome();
        this.telefone = pessoa.getTelefone();
        this.cpf = pessoa.getCpf();
    }

}
