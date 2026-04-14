package com.example.quizcard.flinkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class SubjectSuccessRate implements Serializable {

    String accountId;

    String subject;

    double successRate;

    String updateTime;

}
