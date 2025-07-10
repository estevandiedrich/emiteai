package com.emiteai.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
// TODO: Adicionar @Audited quando Envers estiver totalmente configurado
// import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
// @Audited // Temporariamente comentado - será habilitado após configuração completa
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String estado;

    @OneToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;
}
