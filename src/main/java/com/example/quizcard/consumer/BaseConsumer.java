package com.example.quizcard.consumer;

import kafka.utils.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class BaseConsumer {
    Logger logger = Logger.getLogger(BaseConsumer.class.getName());
    @KafkaListener(topics = "quiz-results-topic", groupId = "quiz-result-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumer(List<ConsumerRecord<String, JsonObject>> records, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String,JsonObject> record: records) {
            String key = record.key();
            JsonObject message = record.value();
            logger.info("Received message: " + key + ":" + message);
        }
        acknowledgment.acknowledge();
    }

}
