package com.example.quizcard.flinkapp.source;

import com.example.quizcard.flinkapp.model.Attempt;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaSourceBuilder {

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokers;

    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    public KafkaSource<Attempt> build(String topic) {
        return KafkaSource.<Attempt>builder()
                .setBootstrapServers(brokers)
                .setTopics(topic)
                .setGroupId(group)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new EventRecordDeserializer())
                .build();
    }
}
