package com.example.quizcard.flinkapp.source;

import com.example.assessment.StudentAssessment;
import com.example.quizcard.flinkapp.util.CredentialManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaSourceBuilder {
    @Autowired
    CredentialManager credentialManager;

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokers;

    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    @Value("${spring.confluent.schema-registry.registry-url}")
    private String registryUrl;

    public KafkaSource<StudentAssessment> build(String topic) {
        JsonNode kafkaSecret = credentialManager.fetchSecret("flinkstreamprocesssvc/confluentcluster");
        String kafkaApiKey = kafkaSecret.get("apiKey").asText();
        String kafkaApiSecret = kafkaSecret.get("apiSecret").asText();
        JsonNode registrySecret = credentialManager.fetchSecret("flinkstreamprocesssvc/schemaregistry");
        String registryApiKey = registrySecret.get("apiKey").asText();
        String registryApiSecret = registrySecret.get("apiSecret").asText();

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