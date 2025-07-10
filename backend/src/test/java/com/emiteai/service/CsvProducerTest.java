package com.emiteai.service;

import com.emiteai.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

class CsvProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CsvProducer csvProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnviarMensagem_DeveEnviarMensagemParaFila() {
        // Given
        String mensagem = "Gerar CSV";

        // When
        csvProducer.enviarMensagem(mensagem);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.QUEUE_NAME, mensagem);
    }

    @Test
    void testEnviarMensagem_ComMensagemVazia() {
        // Given
        String mensagem = "";

        // When
        csvProducer.enviarMensagem(mensagem);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.QUEUE_NAME, mensagem);
    }

    @Test
    void testEnviarMensagem_ComMensagemNula() {
        // Given
        String mensagem = null;

        // When
        csvProducer.enviarMensagem(mensagem);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.QUEUE_NAME, mensagem);
    }
}
