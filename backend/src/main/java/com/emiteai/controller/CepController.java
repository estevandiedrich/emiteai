package com.emiteai.controller;

import com.emiteai.dtos.EnderecoDTO;
import com.emiteai.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cep")
@Tag(name = "CEP", description = "Consulta de endereços via ViaCEP")
public class CepController {

    @Autowired
    private ViaCepService viaCepService;

    @Operation(summary = "Buscar dados do endereço por CEP")
    @GetMapping("/{cep}")
    public EnderecoDTO buscarEndereco(@PathVariable String cep) {
        return viaCepService.buscarEnderecoPorCep(cep);
    }
}
