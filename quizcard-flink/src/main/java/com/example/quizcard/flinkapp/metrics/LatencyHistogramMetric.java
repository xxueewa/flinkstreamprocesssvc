package com.example.quizcard.flinkapp.metrics;

import com.codahale.metrics.SlidingWindowReservoir;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.dropwizard.metrics.DropwizardHistogramWrapper;
import org.apache.flink.metrics.Histogram;

public class LatencyHistogramMetric extends RichMapFunction<Long, Long> {
    private transient Histogram histogram;

    @Override
    public void open(OpenContext ctx) {
        com.codahale.metrics.Histogram dropwizardHistogram =
                new com.codahale.metrics.Histogram(new SlidingWindowReservoir(500));

        this.histogram = getRuntimeContext()
                .getMetricGroup()
                .histogram("event.processing.latency.ms", new DropwizardHistogramWrapper(dropwizardHistogram));
    }

    @Override
    public Long map(Long value) {
        this.histogram.update(value);
        return value;
    }
}
