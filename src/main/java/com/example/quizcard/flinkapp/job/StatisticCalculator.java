package com.example.quizcard.flinkapp.job;

import com.example.quizcard.flinkapp.model.Attempt;
import com.example.quizcard.flinkapp.model.Question;
import com.example.quizcard.flinkapp.model.UserProfile;
import com.example.quizcard.flinkapp.util.UserErrorRateDAO;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class StatisticCalculator extends KeyedProcessFunction<String, Attempt, UserProfile> {

    private static final Logger logger = Logger.getLogger(StatisticCalculator.class.getName());

    private static final double ALPHA_FACTOR = 0.2;

    @Autowired
    UserErrorRateDAO userErrorRateDAO;

    @Override
    public void processElement(Attempt attempt, Context context, Collector<UserProfile> collector) throws Exception {

        /* Exponential Moving Average (EMA)
            new_rate = (1 - α) × old_rate + α × new_result
            Where α (alpha) controls how fast the rate reacts to new results. typically α = 0.2 to 0.3.
        */
        UserProfile userProfile = userErrorRateDAO.queryErrorRates(attempt.getAccountId());
        Map<String, Double> errorRates = userProfile.getErrorRates();

        for (Question question : attempt.getQuestions()) {
            String subject = question.getSubject();
            double prev = errorRates.get(subject); // 90% -> 90.0
            double result = 0.0;
            if (!question.getAnswerKey().equals(question.getStudentAnswer())) {
               result = 100.0;
            }
            double updated = (1 - ALPHA_FACTOR) * prev + ALPHA_FACTOR * result;
            errorRates.put(subject, updated);
        }
        
        userProfile.setErrorRates(errorRates);
        collector.collect(userProfile);
        logger.info("Processed: " + attempt.getId());
    }
}
