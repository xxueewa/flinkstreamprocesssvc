package com.example.quizcard.flinkapp.job;

import com.example.quizcard.flinkapp.model.Attempt;
import com.example.quizcard.flinkapp.model.Question;
import com.example.quizcard.flinkapp.model.SubjectSuccessRate;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class StatisticCalculator extends KeyedProcessFunction<String, Attempt, SubjectSuccessRate> {

    private static final Logger logger = Logger.getLogger(StatisticCalculator.class.getName());

    static final double ALPHA_FACTOR = 0.2;

    @Override
    public void processElement(Attempt attempt, Context context, Collector<SubjectSuccessRate> collector) {
        /* Exponential Moving Average (EMA)
            new_rate = (1 - α) × old_rate + α × new_result
            Where α (alpha) controls how fast the rate reacts to new results. typically α = 0.2 to 0.3.
        */
        logger.log(Level.INFO, "Processing attempt: accountId={0}", attempt.getAccountId());
        Map<String, Double> userSuccessRate = attempt.getOriginalSuccessRate();

        Map<String, Double> updatedRates =  calculateSuccessRate(userSuccessRate, attempt);

        updatedRates.forEach((subject, rate) -> {
            SubjectSuccessRate outputMessage = new SubjectSuccessRate();
            outputMessage.setAccountId(attempt.getAccountId());
            outputMessage.setSubject(subject);
            outputMessage.setSuccessRate(rate);
            outputMessage.setUpdateTime(String.valueOf(System.currentTimeMillis()));
            collector.collect(outputMessage);  // called N times, emits N records downstream
        });
    }

    private Map<String, Double> calculateSuccessRate(Map<String, Double> originalSuccessRate, Attempt attempt) {
        for (Question question : attempt.getQuestions()) {
            String subject = question.getSubject();
            double prev =  originalSuccessRate.get(subject); // 90% -> 90.0
            double result = 100.0;
            if (!question.getAnswerKey().equals(question.getStudentAnswer())) {
               result = 0.0;
            }
            double updated = (1 - ALPHA_FACTOR) * prev + ALPHA_FACTOR * result;
            originalSuccessRate.put(subject, updated);
        }
        return originalSuccessRate;
    }
}
