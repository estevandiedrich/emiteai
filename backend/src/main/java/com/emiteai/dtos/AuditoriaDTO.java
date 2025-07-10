package com.emiteai.dtos;

import com.emiteai.entities.Auditoria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuditoriaDTO {

    private Long id;
    private LocalDateTime timestampRequisicao;
    private String metodoHttp;
    private String endpoint;
    private String ipOrigem;
    private String userAgent;
    private String dadosRequisicao;
    private Integer statusResposta;
    private String dadosResposta;
    private Long tempoProcessamento;
    private String erro;
    private String usuarioIdentificado;

    public AuditoriaDTO(Auditoria auditoria) {
        this.id = auditoria.getId();
        this.timestampRequisicao = auditoria.getTimestampRequisicao();
        this.metodoHttp = auditoria.getMetodoHttp();
        this.endpoint = auditoria.getEndpoint();
        this.ipOrigem = auditoria.getIpOrigem();
        this.userAgent = auditoria.getUserAgent();
        this.dadosRequisicao = auditoria.getDadosRequisicao();
        this.statusResposta = auditoria.getStatusResposta();
        this.dadosResposta = auditoria.getDadosResposta();
        this.tempoProcessamento = auditoria.getTempoProcessamento();
        this.erro = auditoria.getErro();
        this.usuarioIdentificado = auditoria.getUsuarioIdentificado();
    }
}
