package com.example.quizcard.flinkapp;

import com.example.quizcard.flinkapp.job.FlinkStreamingJob;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
public class FlinkJobLauncher implements ApplicationRunner {

    private final Logger logger = Logger.getLogger(FlinkJobLauncher.class.getName());

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    private FlinkStreamingJob flinkStreamingJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        executor.submit(() -> {
            try {
                flinkStreamingJob.run();
            } catch (Exception e) {
                logger.severe("Exception happens while launching the flink job: " + e);
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
