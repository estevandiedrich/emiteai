package com.emiteai.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@Audited
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String telefone;

    @Column(unique = true)
    private String cpf;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL)
    private Endereco endereco;

}
