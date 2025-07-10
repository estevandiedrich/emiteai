package com.emiteai.controller;

import com.emiteai.dtos.AuditoriaDTO;
import com.emiteai.entities.Auditoria;
import com.emiteai.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoria", description = "APIs para consulta de logs de auditoria do sistema")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @Operation(summary = "Buscar auditorias recentes", description = "Retorna auditorias das últimas N horas")
    @GetMapping("/recentes")
    public ResponseEntity<List<AuditoriaDTO>> buscarAuditoriasRecentes(
            @Parameter(description = "Número de horas para buscar (padrão: 24)")
            @RequestParam(defaultValue = "24") int horas) {
        
        List<Auditoria> auditorias = auditoriaService.buscarAuditoriasRecentes(horas);
        List<AuditoriaDTO> dtos = auditorias.stream()
                .map(AuditoriaDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar auditorias por endpoint")
    @GetMapping("/endpoint")
    public ResponseEntity<List<AuditoriaDTO>> buscarPorEndpoint(
            @Parameter(description = "Endpoint para buscar (busca parcial)")
            @RequestParam String endpoint) {
        
        List<Auditoria> auditorias = auditoriaService.buscarAuditoriasPorEndpoint(endpoint);
        List<AuditoriaDTO> dtos = auditorias.stream()
                .map(AuditoriaDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar auditorias por período")
    @GetMapping("/periodo")
    public ResponseEntity<List<AuditoriaDTO>> buscarPorPeriodo(
            @Parameter(description = "Data/hora de início (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data/hora de fim (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        
        List<Auditoria> auditorias = auditoriaService.buscarAuditoriasPorPeriodo(inicio, fim);
        List<AuditoriaDTO> dtos = auditorias.stream()
                .map(AuditoriaDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obter estatísticas de auditoria")
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(
            @Parameter(description = "Número de horas para análise (padrão: 24)")
            @RequestParam(defaultValue = "24") int horas) {
        
        Map<String, Object> estatisticas = new HashMap<>();
        
        // Total de requisições
        Long totalRequisicoes = auditoriaService.contarRequisicoesUltimasHoras(horas);
        estatisticas.put("totalRequisicoes", totalRequisicoes);
        
        // Estatísticas por status
        List<Object[]> statusStats = auditoriaService.obterEstatisticasStatus(horas);
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Object[] stat : statusStats) {
            Integer status = (Integer) stat[0];
            Long count = (Long) stat[1];
            statusDistribution.put(status != null ? status.toString() : "null", count);
        }
        estatisticas.put("distribuicaoStatus", statusDistribution);
        
        // Período analisado
        estatisticas.put("periodoHoras", horas);
        estatisticas.put("timestampConsulta", LocalDateTime.now());
        
        return ResponseEntity.ok(estatisticas);
    }

    @Operation(summary = "Limpar auditorias antigas", description = "Remove auditorias mais antigas que N dias")
    @DeleteMapping("/limpar")
    public ResponseEntity<Map<String, Object>> limparAuditoriasAntigas(
            @Parameter(description = "Número de dias para manter (padrão: 30)")
            @RequestParam(defaultValue = "30") int dias) {
        
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);
        
        // Contar registros antes da limpeza
        List<Auditoria> auditoriasAntigas = auditoriaService.buscarAuditoriasPorPeriodo(
            LocalDateTime.now().minusYears(10), limite);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("registrosIdentificados", auditoriasAntigas.size());
        resultado.put("limiteData", limite);
        resultado.put("observacao", "Funcionalidade de limpeza identificada. Implementar exclusão conforme política de retenção.");
        
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Health check do sistema de auditoria")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificar se conseguimos fazer uma consulta básica
            Long count = auditoriaService.contarRequisicoesUltimasHoras(1);
            health.put("status", "UP");
            health.put("requisicoesUltimaHora", count);
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("erro", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(503).body(health);
        }
    }
}
