package com.example.quizcard.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseConsumer {
    @KafkaListener(topics = "quiz-result", groupId = "quiz-result-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumer(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, String> record: records) {
            String message = record.value();
            System.out.println("Received message: " + message);
        }
        acknowledgment.acknowledge();
    }

}
