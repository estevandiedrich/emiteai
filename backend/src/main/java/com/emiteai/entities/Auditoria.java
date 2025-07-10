package com.emiteai.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp_requisicao", nullable = false)
    private LocalDateTime timestampRequisicao;

    @Column(name = "metodo_http", nullable = false, length = 10)
    private String metodoHttp;

    @Column(name = "endpoint", nullable = false, length = 500)
    private String endpoint;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "dados_requisicao", columnDefinition = "TEXT")
    private String dadosRequisicao;

    @Column(name = "status_resposta")
    private Integer statusResposta;

    @Column(name = "dados_resposta", columnDefinition = "TEXT")
    private String dadosResposta;

    @Column(name = "tempo_processamento")
    private Long tempoProcessamento; // em millisegundos

    @Column(name = "erro", columnDefinition = "TEXT")
    private String erro;

    @Column(name = "usuario_identificado", length = 100)
    private String usuarioIdentificado; // Para futuras implementações de autenticação

    public Auditoria() {
        this.timestampRequisicao = LocalDateTime.now();
    }
}
