package com.emiteai.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RabbitMQConfigTest {

    @Test
    void testQueueCreation() {
        // Given
        RabbitMQConfig config = new RabbitMQConfig();

        // When
        Queue queue = config.queue();

        // Then
        assertNotNull(queue);
        assertEquals(RabbitMQConfig.QUEUE_NAME, queue.getName());
        assertEquals("csv_generation_queue", queue.getName());
    }

    @Test
    void testQueueName() {
        // When & Then
        assertEquals("csv_generation_queue", RabbitMQConfig.QUEUE_NAME);
    }
}
