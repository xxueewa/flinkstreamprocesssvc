package com.example.quizcard.flinkapp.sink;

import com.example.assessment.StudentAssessment;
import com.example.assessment.UserFeatureRecord;
import com.example.quizcard.flinkapp.util.CredentialManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaSinkBuilder {

    @Autowired
    CredentialManager credentialManager;

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokers;

    @Value("${spring.confluent.schema-registry.registry-url}")
    private String registryUrl;

    public <T extends UserFeatureRecord> KafkaSink<UserFeatureRecord> build(String topic) {
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
        registryConfig.put("auto.register.schemas", "false");
        registryConfig.put("use.latest.version", "true");

        return KafkaSink.<UserFeatureRecord>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(ConfluentRegistryAvroSerializationSchema.forSpecific(
                                UserFeatureRecord.class, "user_assessment_feature-value", registryUrl, registryConfig
                        ))
                        .setKeySerializationSchema( message ->
                                message.getAccountId().toString().getBytes(StandardCharsets.UTF_8)
                        )
                        .build()
                )
                .setKafkaProducerConfig(kafkaProps)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
