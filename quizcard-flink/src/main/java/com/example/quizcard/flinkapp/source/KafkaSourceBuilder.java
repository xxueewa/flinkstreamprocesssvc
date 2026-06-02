package com.example.quizcard.flinkapp.source;

import com.example.assessment.StudentAssessment;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaSourceBuilder {

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokers;

    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    private String kafkaApiKey = "";

    private String kafkaApiSecret = "";

    @Value("${spring.confluent.schema-registry.registry-url}")
    private String registryUrl;

    private String registryApiKey = "";

    private String registryApiSecret = "";

    public KafkaSource<StudentAssessment> build(String topic) {
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("security.protocol", "SASL_SSL");
        kafkaProps.setProperty("sasl.mechanism", "PLAIN");
        kafkaProps.setProperty("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + kafkaApiKey + "\" password=\"" + kafkaApiSecret + "\";");

        Map<String, String> registryConfig = new HashMap<>();
        registryConfig.put("schema.registry.url", registryUrl);
        registryConfig.put("basic.auth.credentials.source", "USER_INFO");
        registryConfig.put("basic.auth.user.info", registryApiKey + ":" + registryApiSecret);

        return KafkaSource.<StudentAssessment>builder()
                .setBootstrapServers(brokers)
                .setTopics(topic)
                .setGroupId(group)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setProperties(kafkaProps)
                .setValueOnlyDeserializer(ConfluentRegistryAvroDeserializationSchema.forSpecific(
                        StudentAssessment.class, registryUrl, registryConfig
                ))
                .build();
    }
}