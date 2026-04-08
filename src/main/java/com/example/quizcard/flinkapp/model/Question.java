package com.example.quizcard.flinkapp.model;

import lombok.Data;

@Data
public class Question {
    Integer id;
    Integer sourceId;
    String subject;
    Integer level;
    String text;
    String studentAnswer;
    String answerKey;
}
