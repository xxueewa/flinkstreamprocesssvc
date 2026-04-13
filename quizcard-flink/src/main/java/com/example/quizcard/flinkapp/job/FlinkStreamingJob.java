package com.example.quizcard.flinkapp.job;

import com.example.quizcard.flinkapp.model.Attempt;
import com.example.quizcard.flinkapp.model.UserErrorRate;
//import com.example.quizcard.flinkapp.sink.KafkaSinkBuilder;
import com.example.quizcard.flinkapp.source.KafkaSourceBuilder;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FlinkStreamingJob {

    private final Logger logger = Logger.getLogger(FlinkStreamingJob.class.getName());

    @Autowired
    KafkaSourceBuilder kafkaSourceBuilder;
//
//    @Autowired
//    KafkaSinkBuilder kafkaSinkBuilder;

    @Autowired
    StatisticCalculator statisticCalculator;

    @Value("${spring.kafka.topic}")
    private String topic;

    public void run() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        try {
            KafkaSource<Attempt> source = kafkaSourceBuilder.build(topic);
            DataStream<Attempt> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "source");

            DataStream<String> output = stream
                    .keyBy(Attempt::getId)
                    .process(statisticCalculator)
                    .name("calculator");

            output.print();

//            JdbcStatementBuilder<UserErrorRate> statementBuilder = (statement, record) -> {
//                statement.setDouble(1, record.getErrorRate());
//                statement.setString(2, record.getAccountId());
//                statement.setString(3, record.getSubject());
//            };
//
//            output.sinkTo(kafkaSinkBuilder.userProfileSinker(statementBuilder));

            env.execute("Flink Kafka Streaming Job");
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }
}
