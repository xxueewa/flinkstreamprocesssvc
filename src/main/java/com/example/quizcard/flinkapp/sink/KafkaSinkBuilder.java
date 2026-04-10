//package com.example.quizcard.flinkapp.sink;
//
//import jakarta.annotation.PostConstruct;
//import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
//import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
//import org.apache.flink.connector.jdbc.core.datastream.sink.JdbcSink;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaSinkBuilder {
//
//    @Value("${spring.datasource.url}")
//    private String url;
//
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    private JdbcConnectionOptions jdbcConnectionOptions;
//
//    private JdbcExecutionOptions jdbcExecutionOptions;
//
//    @PostConstruct
//    public void init() {
//        this.jdbcConnectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
//                .withUrl(url)
//                .withDriverName("org.postgresql.Driver")
//                .withUsername(username)
//                .withPassword(password)
//                .build();
//        this.jdbcExecutionOptions = JdbcExecutionOptions.builder()
//                .withBatchSize(100)
//                .withBatchIntervalMs(200)
//                .withMaxRetries(3)
//                .build();
//    }
//
//    public <T> JdbcSink buildSink(String query, JdbcStatementBuilder statementBuilder) {
//        /*
//            Long attempt_id;
//            List<Question> questions;
//            String studentId;
//            String accountId;
//         */
////        PGobject jsonObject = new PGobject();
////        jsonObject.setType("jsonb");
////        jsonObject.setValue(attempt.getQuestions().toString());
//        return JdbcSink.<T>builder()
//                .withQueryStatement(
//                        query,
//                        statementBuilder
//                )
//                .withExecutionOptions(jdbcExecutionOptions)
//                .buildAtLeastOnce(jdbcConnectionOptions);
//    }
//}
