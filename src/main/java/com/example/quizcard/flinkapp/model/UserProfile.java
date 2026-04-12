package com.example.quizcard.flinkapp.model;

import lombok.Data;

import java.util.Map;

@Data
public class UserProfile {
    String accountId;
    String name;
    String email;
    Map<String, Double> errorRates;
    Long lastUpdate;
    Long createdTime;
}
