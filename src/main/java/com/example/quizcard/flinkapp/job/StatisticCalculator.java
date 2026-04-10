package com.example.quizcard.flinkapp.job;

import com.example.quizcard.datamodel.StudentProfile;
import com.example.quizcard.flinkapp.model.Attempt;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class StatisticCalculator extends KeyedProcessFunction<String, Attempt, String> {

    private static final Logger logger = Logger.getLogger(StatisticCalculator.class.getName());

    @Override
    public void processElement(Attempt attempt, Context context, Collector<String> collector) throws Exception {
        collector.collect(attempt.getId());
        logger.info("Processed: %s" + attempt.getId());
    }
}
