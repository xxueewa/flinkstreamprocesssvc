package com.example.quizcard.flinkapp.sink;

import jakarta.annotation.PostConstruct;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.core.datastream.sink.JdbcSink;
import org.springframework.stereotype.Component;

@Component
public class KafkaSinkBuilder {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private JdbcConnectionOptions jdbcConnectionOptions;

    private JdbcExecutionOptions jdbcExecutionOptions;

    @PostConstruct
    public void init() {
        this.jdbcConnectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(url)
                .withDriverName("org.postgresql.Driver")
                .withUsername(username)
                .withPassword(password)
                .build();
        this.jdbcExecutionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(100)
                .withBatchIntervalMs(200)
                .withMaxRetries(3)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> Sink<T> userProfileSinker(JdbcStatementBuilder<T> statementBuilder) {
        String updateQuery = "UPDATE user_error_rate SET error_rate=?, last_update=NOW() WHERE account_id = ? AND subject = ?";
        return JdbcSink.<T>builder()
                .withQueryStatement(
                        updateQuery,
                        statementBuilder
                )
                .withExecutionOptions(jdbcExecutionOptions)
                .buildAtLeastOnce(jdbcConnectionOptions);
    }
}
