package com.example.quizcard.flinkapp.job;

import com.codahale.metrics.SlidingWindowReservoir;
import com.example.assessment.OriginalSuccessRate;
import com.example.assessment.Question;
import com.example.assessment.StudentAssessment;
import com.example.assessment.Subject;
import com.example.assessment.SubjectSuccessRates;
import com.example.assessment.UserFeatureRecord;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardHistogramWrapper;
import org.apache.flink.metrics.Histogram;
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
    private transient Histogram histogram;

    @Override
    public void open(Configuration parameters) {
        ratesState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("success-rates", SubjectSuccessRates.class));
        com.codahale.metrics.Histogram dropwizardHistogram =
                new com.codahale.metrics.Histogram(new SlidingWindowReservoir(500));
        this.histogram = getRuntimeContext()
                .getMetricGroup()
                .histogram("event.processing.latency.ms", new DropwizardHistogramWrapper(dropwizardHistogram));
    }

    @Override
    public void processElement(StudentAssessment attempt, Context context, Collector<UserFeatureRecord> collector) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.log(Level.INFO, "Processing attempt: accountId={0}", attempt.getAccountId());

        SubjectSuccessRates rates = ratesState.value();
        OriginalSuccessRate originRates = attempt.getOriginalSuccessRate();
        /*
          This happens when:
          - No checkpoint exists (first deploy, checkpoint storage wiped) -> build state from original rates
          - Producer crashed and sends zeros -> ignore the payload, use the existing states
        */
        if (rates == null) {
            rates = new SubjectSuccessRates();
            OriginalSuccessRate originalSuccessRate = attempt.getOriginalSuccessRate();
            rates.setChemistry(originalSuccessRate.getChemistry());
            rates.setBiology(originalSuccessRate.getBiology());
            rates.setLaw(originalSuccessRate.getLaw());
            rates.setBusiness(originalSuccessRate.getBusiness());
            rates.setHealth(originalSuccessRate.getHealth());
            rates.setEngineering(originalSuccessRate.getEngineering());
            rates.setHistory(originalSuccessRate.getHistory());
            rates.setPhilosophy(originalSuccessRate.getPhilosophy());
            rates.setEconomics(originalSuccessRate.getEconomics());
            rates.setPsychology(originalSuccessRate.getPsychology());
            rates.setComputerScience(originalSuccessRate.getComputerScience());
            rates.setPhysics(originalSuccessRate.getPhysics());
            rates.setMath(originalSuccessRate.getMath());
            rates.setOther(originalSuccessRate.getOther());
        }

        for (Question q : attempt.getQuestions()) {
            double result = q.getAnswerKey().toString().equals(q.getStudentAnswer().toString()) ? 1.0 : 0.0;
            applyEma(rates, q.getSubject(), result);
        }

        ratesState.update(rates);

        collector.collect(new UserFeatureRecord(
                attempt.getAccountId(),
                rates,
                Instant.now(),
                Instant.now()
        ));
        long endTime = System.currentTimeMillis();
        histogram.update(endTime - startTime);
    }

    private void applyEma(SubjectSuccessRates rates, Subject subject, double result) {
        switch (subject) {
            case chemistry:
                rates.setChemistry(ema(rates.getChemistry(), result));
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
            case other:
                rates.setOther(ema(rates.getOther(), result));
                break;
            default:
                logger.log(Level.WARNING, "Unknown subject: {0}", subject);
        }
    }

    private double ema(double oldRate, double result) {
        return (1 - ALPHA_FACTOR) * oldRate + ALPHA_FACTOR * result;
    }
}