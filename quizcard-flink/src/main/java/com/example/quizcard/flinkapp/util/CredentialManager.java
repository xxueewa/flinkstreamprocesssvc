package com.example.quizcard.flinkapp.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class CredentialManager {

    private static final Logger logger = Logger.getLogger(CredentialManager.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, JsonNode> cache = new ConcurrentHashMap<>();
    private final Region region = Region.of("us-east-2");

    public JsonNode fetchSecret(String secretName) {
        return cache.computeIfAbsent(secretName, this::loadSecret);
    }

    private JsonNode loadSecret(String secretName) {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secret = getSecretValueResponse.secretString();
            JsonNode secretResult = mapper.readTree(secret);
            return secretResult;
        } catch (JsonMappingException e) {
            logger.severe("Fail to parse the response: " + e.getMessage());
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw new RuntimeException("Fail to get the credential");
        }
        return null;
    }
}
