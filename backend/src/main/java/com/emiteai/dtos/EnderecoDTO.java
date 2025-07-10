package com.emiteai.dtos;

import com.emiteai.entities.Endereco;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnderecoDTO {

    private Long id;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String estado;

    public EnderecoDTO(Endereco endereco) {
        this.id = endereco.getId();
        this.numero = endereco.getNumero();
        this.complemento = endereco.getComplemento();
        this.cep = endereco.getCep();
        this.bairro = endereco.getBairro();
        this.municipio = endereco.getMunicipio();
        this.estado = endereco.getEstado();
    }
}
