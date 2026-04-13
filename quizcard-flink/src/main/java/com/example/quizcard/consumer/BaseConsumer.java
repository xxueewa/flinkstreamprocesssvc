//package com.example.quizcard.consumer;
//
//import com.example.quizcard.flinkapp.model.Attempt;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.bson.json.JsonObject;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@Service
//public class BaseConsumer {
//    static final Logger logger = Logger.getLogger(BaseConsumer.class.getName());
//    static final ObjectMapper mapper = new ObjectMapper();
//
//    @KafkaListener(topics = "quiz-results-topic", groupId = "quiz-result-group", containerFactory = "kafkaListenerContainerFactory")
//    public void consumer(List<ConsumerRecord<String, JsonObject>> records, Acknowledgment acknowledgment) {
//        for (ConsumerRecord<String, JsonObject> record: records) {
//            String key = record.key();
//            JsonObject message = record.value();
//            try {
//                Attempt attempt = mapper.readValue(message.toString(), Attempt.class);
//                logger.info(attempt.toString());
//            } catch (JsonProcessingException e) {
//                logger.info("Exception happens while parsing the message payload" + e);
//            }
//        }
//        acknowledgment.acknowledge();
//        logger.log(Level.INFO, "Received batch records: {0}", records.size());
//    }
//
//}
