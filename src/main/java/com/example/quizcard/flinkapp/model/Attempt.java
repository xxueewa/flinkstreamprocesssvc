package com.example.quizcard.flinkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Attempt implements Serializable {
    String id;
    String accountId;
    QuestionList questions;
    String timestamp;
}
