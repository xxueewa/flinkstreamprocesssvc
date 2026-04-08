package com.example.quizcard.flinkapp.job;

import com.example.quizcard.flinkapp.model.Attempt;
import com.example.quizcard.flinkapp.sink.KafkaSinkBuilder;
import com.example.quizcard.flinkapp.source.KafkaSourceBuilder;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.logging.Logger;

@Component
public class FlinkStreamingJob {

    static final Logger logger = Logger.getLogger(FlinkStreamingJob.class.getName());

    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    KafkaSinkBuilder sink;

    public void run() {

        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            KafkaSource<String> source = KafkaSourceBuilder.build(topic);
            DataStream<String> stream =
                    env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

            String query = "INSERT INTO quiz_log (user_id, questions, timestamp) VALUES (?, ?, ?)";
            JdbcStatementBuilder<Attempt> statementBuilder = (PreparedStatement statement, Attempt event) -> {
                statement.setString(1, event.getAccountId());
                PGobject jsonObject = new PGobject();
                jsonObject.setType("jsonb");
                jsonObject.setValue(event.getQuestions().toString());
                statement.setObject(2, jsonObject);
                statement.setTimestamp(3, Timestamp.valueOf(event.getTimestamp()));
            };
            stream.sinkTo((Sink<String>) sink.buildSink(query, statementBuilder));

            env.execute("Flink Kafka Streaming Job");
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }
}
