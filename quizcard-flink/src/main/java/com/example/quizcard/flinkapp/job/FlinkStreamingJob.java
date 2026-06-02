package com.example.quizcard.flinkapp.job;

import com.example.assessment.StudentAssessment;
import com.example.assessment.UserFeatureRecord;
import com.example.quizcard.flinkapp.source.KafkaSourceBuilder;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
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

    @Autowired
    StatisticCalculator statisticCalculator;

    @Value("${spring.kafka.topic}")
    private String topic;

    public void run() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        try {
            KafkaSource<StudentAssessment> source = kafkaSourceBuilder.build(topic);
            logger.log(Level.INFO, "Kafka Source Built for: " + topic);
            DataStream<StudentAssessment> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "source");

            DataStream<UserFeatureRecord> output = stream
                    .keyBy(a -> a.getAccountId().toString())
                    .transform("calculator", TypeInformation.of(UserFeatureRecord.class), new KeyedProcessOperator<>(statisticCalculator));

            output.print();

            env.execute("Flink Kafka Streaming Job");
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }
}
