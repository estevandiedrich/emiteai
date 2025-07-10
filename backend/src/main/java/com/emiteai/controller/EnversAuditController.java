package com.emiteai.controller;

import com.emiteai.entities.Pessoa;
import com.emiteai.service.EnversAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envers-audit")
@RequiredArgsConstructor
@Tag(name = "Envers Audit", description = "API para auditoria de entidades usando Hibernate Envers")
public class EnversAuditController {

    private final EnversAuditService enversAuditService;

    @Operation(summary = "Buscar histórico de revisões de uma pessoa")
    @GetMapping("/pessoa/{id}/revisoes")
    public ResponseEntity<List<Number>> getPessoaRevisions(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        List<Number> revisoes = enversAuditService.findRevisionsByPessoa(id);
        return ResponseEntity.ok(revisoes);
    }

    @Operation(summary = "Buscar estado de uma pessoa em uma revisão específica")
    @GetMapping("/pessoa/{id}/revisao/{revision}")
    public ResponseEntity<Pessoa> getPessoaAtRevision(
            @Parameter(description = "ID da pessoa") @PathVariable Long id,
            @Parameter(description = "Número da revisão") @PathVariable Number revision) {
        
        Pessoa pessoa = enversAuditService.findPessoaAtRevision(id, revision);
        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pessoa);
    }

    @Operation(summary = "Buscar pessoas modificadas em um período")
    @GetMapping("/pessoas/modificadas")
    public ResponseEntity<List<Pessoa>> getPessoasModificadasPeriodo(
            @Parameter(description = "Data de início") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @Parameter(description = "Data de fim") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim) {
        
        List<Pessoa> pessoas = enversAuditService.findPessoasModifiedBetween(dataInicio, dataFim);
        return ResponseEntity.ok(pessoas);
    }

    @Operation(summary = "Buscar histórico completo de uma pessoa")
    @GetMapping("/pessoa/{id}/historico")
    public ResponseEntity<List<Object[]>> getPessoaHistorico(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        List<Object[]> historico = enversAuditService.findPessoaHistoryWithRevisionInfo(id);
        return ResponseEntity.ok(historico);
    }

    @Operation(summary = "Buscar todas as operações recentes de pessoas")
    @GetMapping("/pessoas/operacoes")
    public ResponseEntity<List<Object[]>> getAllPessoaOperations() {
        List<Object[]> operacoes = enversAuditService.findAllPessoaOperations();
        return ResponseEntity.ok(operacoes);
    }

    @Operation(summary = "Obter estatísticas de auditoria de uma pessoa")
    @GetMapping("/pessoa/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> getPessoaAuditStats(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevisoes", enversAuditService.countPessoaRevisions(id));
        stats.put("ultimaModificacao", enversAuditService.findLastModificationDate(id));
        
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Health check do sistema Envers")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Testa uma operação simples para verificar se Envers está funcionando
            List<Object[]> operacoes = enversAuditService.findAllPessoaOperations();
            health.put("status", "UP");
            health.put("enversOperational", true);
            health.put("totalOperacoesRecentes", operacoes.size());
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("enversOperational", false);
            health.put("erro", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
        
        return ResponseEntity.ok(health);
    }
}
