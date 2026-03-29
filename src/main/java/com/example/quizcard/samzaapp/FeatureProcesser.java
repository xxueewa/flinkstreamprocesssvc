package com.example.quizcard.samzaapp;

import org.apache.samza.application.SamzaApplication;
import org.apache.samza.application.descriptors.ApplicationDescriptor;

public class FeatureProcesser implements SamzaApplication {
    @Override
    public void describe(ApplicationDescriptor applicationDescriptor) {
        // input stream: kafka topic
        // output store in mongodb

    }
}
