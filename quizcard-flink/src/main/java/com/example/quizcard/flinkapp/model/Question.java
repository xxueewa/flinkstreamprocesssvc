package com.example.quizcard.flinkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Question {
    Integer id;
    Integer sourceId;
    String subject;
    String level;
    String text;
    String studentAnswer;
    String answerKey;
}
