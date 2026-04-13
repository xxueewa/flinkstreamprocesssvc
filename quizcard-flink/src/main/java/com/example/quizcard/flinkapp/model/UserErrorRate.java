package com.example.quizcard.flinkapp.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserErrorRate {

    String accountId;

    String subject;

    double errorRate;

    Timestamp lastUpdate;

    Timestamp createdTime;

}
