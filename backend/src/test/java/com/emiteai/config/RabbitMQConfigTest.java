package com.emiteai.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {

    @Test
    void testQueueCreation() {
        RabbitMQConfig config = new RabbitMQConfig();

        Queue queue = config.queue();

        assertNotNull(queue);
        assertEquals(RabbitMQConfig.QUEUE_NAME, queue.getName());
        assertEquals("csv_generation_queue", queue.getName());
    }

    @Test
    void testQueueName() {
        assertEquals("csv_generation_queue", RabbitMQConfig.QUEUE_NAME);
    }
}
