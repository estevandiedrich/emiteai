package com.emiteai.controller;

import com.emiteai.dtos.PessoaDTO;
import com.emiteai.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@Tag(name = "Pessoa", description = "APIs de cadastro e consulta de pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @Operation(summary = "Cadastrar uma nova pessoa")
    @PostMapping
    public PessoaDTO cadastrar(@RequestBody PessoaDTO pessoaDTO) {
        return pessoaService.cadastrarPessoa(pessoaDTO);
    }

    @Operation(summary = "Listar todas as pessoas")
    @GetMapping
    public List<PessoaDTO> listarTodas() {
        return pessoaService.listarTodas();
    }

    @Operation(summary = "Buscar pessoa por CPF")
    @GetMapping("/cpf/{cpf}")
    public PessoaDTO buscarPorCpf(@PathVariable String cpf) {
        return pessoaService.buscarPorCpf(cpf);
    }

    @Operation(summary = "Buscar pessoa por ID")
    @GetMapping("/{id}")
    public PessoaDTO buscarPorId(@PathVariable Long id) {
        return pessoaService.buscarPorId(id);
    }

    @Operation(summary = "Atualizar pessoa por ID")
    @PutMapping("/{id}")
    public PessoaDTO atualizar(@PathVariable Long id, @RequestBody PessoaDTO pessoaDTO) {
        return pessoaService.atualizarPessoa(id, pessoaDTO);
    }

    @Operation(summary = "Deletar pessoa por ID")
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        pessoaService.deletar(id);
    }
}
