package com.example.quizcard.flinkapp.model;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class UserErrorRate implements Serializable {

    String accountId;

    String subject;

    double errorRate;

    Timestamp lastUpdate;

    Timestamp createdTime;

}
