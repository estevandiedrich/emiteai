package com.emiteai.service;

import com.emiteai.config.RabbitMQConfig;
import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class CsvConsumer {

    @Autowired
    private PessoaRepository pessoaRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receberMensagem(String mensagem) {
        log.info("Recebida mensagem para gerar CSV: {}", mensagem);
        gerarCsv();
        log.info("CSV gerado com sucesso em /tmp/pessoas.csv");
    }

    private void gerarCsv() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        log.info("Encontradas {} pessoas para incluir no CSV", pessoas.size());

        try (PrintWriter writer = new PrintWriter(new File("/tmp/pessoas.csv"))) {
            writer.println("Nome,Telefone,CPF");
            for (Pessoa pessoa : pessoas) {
                writer.printf("%s,%s,%s\n",
                        pessoa.getNome(),
                        pessoa.getTelefone(),
                        pessoa.getCpf()
                );
                log.debug("Adicionada pessoa ao CSV: {}", pessoa.getNome());
            }
        } catch (IOException e) {
            log.error("Erro ao gerar CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }
}
