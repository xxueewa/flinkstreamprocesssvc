package com.example.quizcard.flinkapp.job;

import com.example.quizcard.flinkapp.model.Attempt;
import com.example.quizcard.flinkapp.model.Question;
import com.example.quizcard.flinkapp.model.UserErrorRate;
import com.example.quizcard.flinkapp.model.UserSummary;
import com.example.quizcard.flinkapp.util.UserErrorRateDAO;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class StatisticCalculator extends KeyedProcessFunction<String, Attempt, UserErrorRate> {

    private static final Logger logger = Logger.getLogger(StatisticCalculator.class.getName());

    private static final double ALPHA_FACTOR = 0.2;

    @Autowired
    UserErrorRateDAO userErrorRateDAO;

    @Override
    public void processElement(Attempt attempt, Context context, Collector<UserErrorRate> collector) {
        /* Exponential Moving Average (EMA)
            new_rate = (1 - α) × old_rate + α × new_result
            Where α (alpha) controls how fast the rate reacts to new results. typically α = 0.2 to 0.3.
        */
        logger.log(Level.INFO, "Processing attempt: accountId={0}, questions={1}",
                new Object[]{attempt.getAccountId(), attempt.getQuestions().size()});

        UserSummary userSummary = userErrorRateDAO.queryErrorRates(attempt.getAccountId());
        Map<String, Double> updatedRates = getStringDoubleMap(attempt, userSummary);

        // one Attempt → many UserErrorRate (one per subject)
        updatedRates.forEach((subject, rate) -> {
            UserErrorRate userErrorRate = new UserErrorRate();
            userErrorRate.setAccountId(attempt.getAccountId());
            userErrorRate.setSubject(subject);
            userErrorRate.setErrorRate(rate);
            collector.collect(userErrorRate);  // called N times, emits N records downstream
            logger.log(Level.INFO, "Emitted: accountId={0}, subject={1}, errorRate={2}",
                    new Object[]{attempt.getAccountId(), subject, rate});
        });
    }

    private Map<String, Double> getStringDoubleMap(Attempt attempt, UserSummary userSummary) {
        Map<String, Double> errorRates = userSummary.getErrorRates();

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
        return errorRates;
    }
}
