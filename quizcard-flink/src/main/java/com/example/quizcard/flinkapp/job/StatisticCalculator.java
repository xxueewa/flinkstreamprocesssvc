package com.example.quizcard.flinkapp.job;

import com.example.assessment.Question;
import com.example.assessment.StudentAssessment;
import com.example.assessment.Subject;
import com.example.assessment.SubjectSuccessRates;
import com.example.assessment.UserFeatureRecord;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class StatisticCalculator extends KeyedProcessFunction<String, StudentAssessment, UserFeatureRecord> {

    private static final Logger logger = Logger.getLogger(StatisticCalculator.class.getName());
    static final double ALPHA_FACTOR = 0.2;

    private transient ValueState<SubjectSuccessRates> ratesState;
    private transient ValueState<Long> createdTimeState;

    @Override
    public void open(Configuration parameters) {
        ratesState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("success-rates", SubjectSuccessRates.class));
        createdTimeState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("created-time", Long.class));
    }

    @Override
    public void processElement(StudentAssessment attempt, Context context, Collector<UserFeatureRecord> collector) throws Exception {
        logger.log(Level.INFO, "Processing attempt: accountId={0}", attempt.getAccountId());

        SubjectSuccessRates rates = ratesState.value();
        if (rates == null) {
            rates = new SubjectSuccessRates();
        }

        Long createdTimeMs = createdTimeState.value();
        if (createdTimeMs == null) {
            createdTimeMs = System.currentTimeMillis();
            createdTimeState.update(createdTimeMs);
        }

        for (Question q : attempt.getQuestions()) {
            double result = q.getAnswerKey().toString().equals(q.getStudentAnswer().toString()) ? 1.0 : 0.0;
            applyEma(rates, q.getSubject(), result);
        }

        ratesState.update(rates);

        collector.collect(new UserFeatureRecord(
                attempt.getAccountId(),
                rates,
                Instant.ofEpochMilli(createdTimeMs),
                Instant.now()
        ));
    }

    private void applyEma(SubjectSuccessRates rates, Subject subject, double result) {
        switch (subject) {
            case chemistry:
                rates.setChemistry(ema(rates.getChemistry(), result));
                break;
            case other:
                rates.setOther(ema(rates.getOther(), result));
                break;
            case biology:
                rates.setBiology(ema(rates.getBiology(), result));
                break;
            case law:
                rates.setLaw(ema(rates.getLaw(), result));
                break;
            case business:
                rates.setBusiness(ema(rates.getBusiness(), result));
                break;
            case health:
                rates.setHealth(ema(rates.getHealth(), result));
                break;
            case engineering:
                rates.setEngineering(ema(rates.getEngineering(), result));
                break;
            case history:
                rates.setHistory(ema(rates.getHistory(), result));
                break;
            case philosophy:
                rates.setPhilosophy(ema(rates.getPhilosophy(), result));
                break;
            case economics:
                rates.setEconomics(ema(rates.getEconomics(), result));
                break;
            case psychology:
                rates.setPsychology(ema(rates.getPsychology(), result));
                break;
            case computer_science:
                rates.setComputerScience(ema(rates.getComputerScience(), result));
                break;
            case physics:
                rates.setPhysics(ema(rates.getPhysics(), result));
                break;
            case math:
                rates.setMath(ema(rates.getMath(), result));
                break;
            default:
                logger.log(Level.WARNING, "Unknown subject: {0}", subject);
        }
    }

    private double ema(double oldRate, double result) {
        return (1 - ALPHA_FACTOR) * oldRate + ALPHA_FACTOR * result;
    }
}