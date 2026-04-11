package com.example.quizcard.flinkapp.model;

import lombok.Data;

import java.util.Map;

@Data
public class UserProfile {
    String id;
    String accountId;
    Map<String, Double> errorRates;
    Long lastUpdate;
    Long createdTime;
}
