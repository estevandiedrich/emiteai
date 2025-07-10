package com.emiteai.controller;

import com.emiteai.service.CsvProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatorio", description = "APIs de relatórios")
public class RelatorioController {

    @Autowired
    private CsvProducer csvProducer;

    @Operation(summary = "Solicitar geração assíncrona de CSV")
    @PostMapping("/csv")
    public ResponseEntity<String> gerarCsv() {
        csvProducer.enviarMensagem("Gerar CSV");
        return ResponseEntity.accepted().body("Relatório CSV sendo gerado");
    }

    @Operation(summary = "Download do CSV gerado")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadCsv() throws IOException {
        File file = new File("/tmp/pessoas.csv");
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pessoas.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
