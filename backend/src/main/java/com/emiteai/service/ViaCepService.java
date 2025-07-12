package com.emiteai.service;

import com.emiteai.dtos.EnderecoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate = new RestTemplate();

    public EnderecoDTO buscarEnderecoPorCep(String cep) {
        try {
            // Remove caracteres não numéricos do CEP
            String cleanCep = cep.replaceAll("\\D", "");
            
            // Valida se o CEP tem 8 dígitos
            if (cleanCep.length() != 8) {
                throw new RuntimeException("CEP deve conter 8 dígitos");
            }
            
            String url = "https://viacep.com.br/ws/" + cleanCep + "/json/";
            
            // Usar Map para obter resposta dinâmica
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || response.containsKey("erro")) {
                throw new RuntimeException("CEP não encontrado");
            }
            
            // Mapeia para o nosso DTO usando o Map
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            enderecoDTO.setCep(formatCep(cleanCep));
            enderecoDTO.setComplemento((String) response.get("complemento"));
            enderecoDTO.setBairro((String) response.get("bairro"));
            
            String localidade = (String) response.get("localidade");
            String uf = (String) response.get("uf");
            
            enderecoDTO.setMunicipio(localidade != null ? localidade : "São Paulo TESTE");
            enderecoDTO.setEstado(uf != null ? uf : "SP TESTE");
            
            return enderecoDTO;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
    
    private String formatCep(String cep) {
        if (cep.length() == 8) {
            return cep.substring(0, 5) + "-" + cep.substring(5);
        }
        return cep;
    }
}

