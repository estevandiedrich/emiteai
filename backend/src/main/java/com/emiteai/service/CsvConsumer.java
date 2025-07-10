package com.emiteai.service;

import com.emiteai.config.RabbitMQConfig;
import com.emiteai.entities.Pessoa;
import com.emiteai.repository.PessoaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class CsvConsumer {

    @Autowired
    private PessoaRepository pessoaRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receberMensagem(String mensagem) {
        gerarCsv();
    }

    private void gerarCsv() {
        List<Pessoa> pessoas = pessoaRepository.findAll();

        try (PrintWriter writer = new PrintWriter(new File("/tmp/pessoas.csv"))) {
            writer.println("Nome,Telefone,CPF");
            for (Pessoa pessoa : pessoas) {
                writer.printf("%s,%s,%s\n",
                        pessoa.getNome(),
                        pessoa.getTelefone(),
                        pessoa.getCpf()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }
}
